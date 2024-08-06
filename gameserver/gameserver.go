package gameserver

import (
	"encoding/json"
	"gameserver/events"
	"gameserver/games"
	"github.com/google/uuid"
	"golang.org/x/net/websocket"
	"io"
	"log/slog"
	"strings"
)

type Server struct {
	websocket.Server

	Coordinator games.Coordinator
}

func NewServer() *Server {
	var s Server
	s.Server = websocket.Server{
		Handler: s.handleWSConn,
	}
	return &s
}

func (s *Server) handleWSConn(ws *websocket.Conn) {
	defer func() { _ = ws.Close() }()

	var gameID uuid.UUID
	if ws.Request().URL.Path == "/" {
		gameID = s.Coordinator.CreateNewGame()
	} else {
		pathID := strings.Trim(ws.Request().URL.Path, "/")

		var err error
		gameID, err = uuid.Parse(pathID)
		if err != nil {
			_, _ = io.WriteString(ws, err.Error())
			return
		}
	}

	channels, err := s.Coordinator.AcquireChannels(gameID)
	if err != nil {
		_, _ = io.WriteString(ws, err.Error())
		return
	}

	go func() {
		for {
			var in events.InEvent
			if err = json.NewDecoder(ws).Decode(&in); err != nil {
				slog.Warn("error reading message", "err", err)
				return
			}

			channels.Inbound <- in
		}
	}()

	for {
		out := <-channels.Outbound
		if err = json.NewEncoder(ws).Encode(out); err != nil {
			slog.Warn("error writing message", "err", err)
			return
		}
	}
}
