package types

import "sync"

type SyncSlice[T any] struct {
	mx    sync.Mutex
	items []T
}

func NewSyncSlice[T any]() *SyncSlice[T] {
	return &SyncSlice[T]{}
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

func (s *SyncSlice[T]) Range(f func(int, T) bool) {
	s.mx.Lock()
	cpy := make([]T, len(s.items))
	copy(cpy, s.items)
	s.mx.Unlock()

	for i, item := range cpy {
		if !f(i, item) {
			break
		}
	}
}
