package games

import (
	"gameserver/events"
	"types"
)

type Game struct {
	PlayerConnChannels types.SyncSlice[*ConnChannels]
}

func (g *Game) Run() {
	for {
		g.PlayerConnChannels.Range(func(_ int, channels *ConnChannels) bool {
			select {
			case e := <-channels.Inbound:
				switch data := e.Data.(type) {
				case events.MakeGuess:
					g.MakeGuess(channels, data)
				}
			default:
			}

			return true
		})
	}
}

func (g *Game) ConnectPlayer() *ConnChannels {
	channels := NewConnChannels()
	g.PlayerConnChannels.Push(channels)
	return channels
}

func (g *Game) Broadcast(event events.OutEvent) {
	g.PlayerConnChannels.Range(func(_ int, channels *ConnChannels) bool {
		channels.Outbound <- event
		return true
	})
}

func (g *Game) MakeGuess(channels *ConnChannels, data events.MakeGuess) {
	g.Broadcast(events.OutEvent{
		Tag: "newGuess",
		Data: events.NewGuess{
			PlayerID: channels.PlayerID,
			Guess:    data.Guess,
		},
	})
}
