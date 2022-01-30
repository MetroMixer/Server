package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class ApiWebSocket {

    private Gson gson;
    public ApiWebSocket(Gson gson) {
        this.gson = gson;
    }

    @OnWebSocketConnect
    public void connected(Session session) throws IOException {

    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {

    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {

    }
}
