package types

import "sync"

type SyncMap[K, V any] sync.Map

func (s *SyncMap[K, V]) Put(key K, value V) {
	(*sync.Map)(s).Store(key, value)
}

func (s *SyncMap[K, V]) Get(key K) (V, bool) {
	value, ok := (*sync.Map)(s).Load(key)
	if !ok {
		return *new(V), ok
	}

	return value.(V), ok
}
