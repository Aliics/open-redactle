package games

import (
	"gameserver/events"
	"github.com/google/uuid"
)

type ConnChannels struct {
	PlayerID uuid.UUID
	Inbound  chan events.InEvent
	Outbound chan events.OutEvent

	IsClosed bool
}

func NewConnChannels() *ConnChannels {
	return &ConnChannels{
		uuid.New(),
		make(chan events.InEvent),
		make(chan events.OutEvent),
		false,
	}
}

func (c *ConnChannels) Close() error {
	if c.IsClosed {
		return nil
	}

	c.IsClosed = true
	close(c.Inbound)
	close(c.Outbound)
	return nil
}
