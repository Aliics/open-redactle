package events

import "github.com/google/uuid"

type OutEvent struct {
	Tag  string `json:"tag"`
	Data any    `json:"data"`
}

type CurrentGameState struct {
	GameID    uuid.UUID   `json:"gameId"`
	PlayerIDs []uuid.UUID `json:"playerIds"`
}

type NewGuess struct {
	PlayerID uuid.UUID `json:"playerId"`
	Guess    string    `json:"guess"`
}
