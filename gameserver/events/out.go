package events

import (
	"gameserver/state"
	"github.com/google/uuid"
	"reflect"
)

type OutEvent struct {
	Tag  string `json:"tag"`
	Data any    `json:"data"`
}

func NewOutEvent(v any) OutEvent {
	tag := reflect.TypeOf(v).Name()
	return OutEvent{tag, v}
}

type CurrentGameState struct {
	GameID      uuid.UUID     `json:"gameId"`
	PlayerIDs   []uuid.UUID   `json:"playerIds"`
	GuessesMade []state.Guess `json:"guessesMade"`
}

type NewGuess struct {
	PlayerID uuid.UUID `json:"playerId"`
	Guess    string    `json:"guess"`
}

type PlayerConnected struct {
	PlayerID uuid.UUID `json:"playerId"`
}

type PlayerDisconnected struct {
	PlayerID uuid.UUID `json:"playerId"`
}
