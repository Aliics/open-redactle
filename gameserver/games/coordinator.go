package games

import (
	"errors"
	"github.com/google/uuid"
	"types"
)

type Coordinator struct {
	games types.SyncMap[uuid.UUID, *Game]
}

func (c *Coordinator) CreateNewGame() uuid.UUID {
	game := Game{}
	go game.Run()

	gameID := uuid.New()
	c.games.Put(gameID, &game)

	return gameID
}

func (c *Coordinator) AcquireChannels(gameID uuid.UUID) (*ConnChannels, error) {
	game, ok := c.games.Get(gameID)
	if !ok {
		return nil, errors.New("game not found")
	}

	return (*game).ConnectPlayer(), nil
}
