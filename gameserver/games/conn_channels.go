package games

import (
	"gameserver/events"
	"github.com/google/uuid"
)

type ConnChannels struct {
	PlayerID uuid.UUID
	Inbound  chan events.InEvent
	Outbound chan events.OutEvent
}

func NewConnChannels() *ConnChannels {
	return &ConnChannels{
		uuid.New(),
		make(chan events.InEvent),
		make(chan events.OutEvent),
	}
}
