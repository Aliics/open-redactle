package games

import (
	"gameserver/events"
	"github.com/google/uuid"
	"types"
)

type Game struct {
	ID uuid.UUID

	PlayerConnChannels types.SyncSlice[*ConnChannels]
}

func (g *Game) Run() {
	for {
		g.PlayerConnChannels.Range(func(channels *ConnChannels) {
			select {
			case e := <-channels.Inbound:
				switch data := e.Data.(type) {
				case events.MakeGuess:
					g.MakeGuess(channels, data)
				}
			default:
			}
		})
	}
}

func (g *Game) ConnectPlayer() *ConnChannels {
	channels := NewConnChannels()
	g.PlayerConnChannels.Push(channels)

	go g.SendCurrentGameState(channels)
	go g.Broadcast(events.NewOutEvent(events.PlayerConnected{
		PlayerID: channels.PlayerID,
	}))

	return channels
}

func (g *Game) Broadcast(event events.OutEvent) {
	g.PlayerConnChannels.Range(func(channels *ConnChannels) {
		channels.Outbound <- event
	})
}

func (g *Game) MakeGuess(channels *ConnChannels, data events.MakeGuess) {
	g.Broadcast(events.NewOutEvent(events.NewGuess{
		PlayerID: channels.PlayerID,
		Guess:    data.Guess,
	}))
}

func (g *Game) SendCurrentGameState(channels *ConnChannels) {
	func() {
		var playerIDs []uuid.UUID
		g.PlayerConnChannels.Range(func(channels *ConnChannels) {
			playerIDs = append(playerIDs, channels.PlayerID)
		})

		channels.Outbound <- events.NewOutEvent(events.CurrentGameState{
			GameID:    g.ID,
			PlayerIDs: playerIDs,
		})
	}()
}
