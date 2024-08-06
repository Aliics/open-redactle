package main

import (
	"gameserver"
	"log/slog"
	"net/http"
)

func main() {
	if err := http.ListenAndServe(":8080", gameserver.NewServer()); err != nil {
		slog.Error("failure in s", "err", err)
	}
}
