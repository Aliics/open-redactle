package types

import (
	"slices"
	"sync"
)

type SyncSlice[T any] struct {
	mx    sync.Mutex
	items []T
}

func (s *SyncSlice[T]) Get(i int) T {
	s.mx.Lock()
	defer s.mx.Unlock()
	return s.items[i]
}

func (s *SyncSlice[T]) Push(value T) {
	s.mx.Lock()
	defer s.mx.Unlock()
	s.items = append(s.items, value)
}

func (s *SyncSlice[T]) DeleteFunc(pred func(T) bool) {
	s.mx.Lock()
	defer s.mx.Unlock()
	s.items = slices.DeleteFunc(s.items, pred)
}

func (s *SyncSlice[T]) Range(f func(T)) {
	s.mx.Lock()
	cpy := make([]T, len(s.items))
	copy(cpy, s.items)
	s.mx.Unlock()

	for _, item := range cpy {
		f(item)
	}
}
