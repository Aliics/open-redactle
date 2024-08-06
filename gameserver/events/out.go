package events

import "github.com/google/uuid"

type OutEvent struct {
	Tag  string `json:"tag"`
	Data any    `json:"data"`
}

type NewGuess struct {
	PlayerID uuid.UUID `json:"playerId"`
	Guess    string    `json:"guess"`
}
