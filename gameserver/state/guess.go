package state

import "github.com/google/uuid"

type Guess struct {
	PlayerID uuid.UUID `json:"playerId"`
	Guess    string    `json:"guess"`
}
