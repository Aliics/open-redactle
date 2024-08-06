package events

import (
	"encoding/json"
	"fmt"
	"github.com/google/uuid"
	"reflect"
)

// inEventTagMapping maps all of our input tags to their respective data types.
var inEventTagMapping = map[string]func() any{
	"MakeGuess": func() any { return &MakeGuess{} },
}

type MakeGuess struct {
	Guess string `json:"guess"`
}

type InEvent struct {
	Tag      string    `json:"tag"`
	PlayerID uuid.UUID `json:"playerId"`

	RawData json.RawMessage `json:"data"`
	Data    any             `json:"-"`
}

func (i *InEvent) UnmarshalJSON(bytes []byte) error {
	type inEvent InEvent
	if err := json.Unmarshal(bytes, (*inEvent)(i)); err != nil {
		return err
	}

	f, ok := inEventTagMapping[i.Tag]
	if !ok {
		return fmt.Errorf("unknown tag %s", i.Tag)
	}
	v := f()

	if err := json.Unmarshal(i.RawData, v); err != nil {
		return err
	}

	// Generic de-pointer-fication.
	i.Data = reflect.ValueOf(v).Elem().Interface()

	return nil
}
