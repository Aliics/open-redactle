package games

import (
	"gameserver/events"
	"gameserver/state"
	"github.com/google/uuid"
	"types"
)

type Game struct {
	ID uuid.UUID

	PlayerConnChannels types.SyncSlice[*ConnChannels]
	GuessesMade        types.SyncSlice[state.Guess]
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

func (g *Game) MakeGuess(channels *ConnChannels, data events.MakeGuess) {
	if g.GuessesMade.Exists(func(guess state.Guess) bool { return guess.Guess == data.Guess }) {
		return
	}

	g.GuessesMade.Push(state.Guess{
		PlayerID: channels.PlayerID,
		Guess:    data.Guess,
	})

	g.Broadcast(events.NewOutEvent(events.NewGuess{
		PlayerID: channels.PlayerID,
		Guess:    data.Guess,
	}))
}

func (g *Game) SendCurrentGameState(channels *ConnChannels) {
	var playerIDs []uuid.UUID
	for _, playerChannels := range g.PlayerConnChannels.Now() {
		playerIDs = append(playerIDs, playerChannels.PlayerID)
	}

	channels.Outbound <- events.NewOutEvent(events.CurrentGameState{
		GameID:      g.ID,
		PlayerIDs:   playerIDs,
		GuessesMade: g.GuessesMade.Now(),
	})
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

func (g *Game) DisconnectPlayer(channels *ConnChannels) {
	defer func() { _ = channels.Close() }() // Should have already happened, but just in case. ;)

	g.PlayerConnChannels.DeleteFunc(func(stored *ConnChannels) bool {
		return stored.PlayerID == channels.PlayerID
	})
	go g.Broadcast(events.NewOutEvent(events.PlayerDisconnected{
		PlayerID: channels.PlayerID,
	}))
}

func (g *Game) Broadcast(event events.OutEvent) {
	g.PlayerConnChannels.Range(func(channels *ConnChannels) {
		channels.Outbound <- event
	})
}
