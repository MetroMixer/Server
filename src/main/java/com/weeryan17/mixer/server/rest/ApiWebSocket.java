package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weeryan17.mixer.server.models.ApiSession;
import com.weeryan17.mixer.server.models.managers.ApiManager;
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
        String key = session.getUpgradeRequest().getHeader("key");
        ApiSession apiSession = ApiManager.getInstance().getSessionFromKey(key);
        if (apiSession == null) {
            session.close();
        }
        JsonObject init = new JsonObject();
        init.addProperty("beat", ApiManager.getInstance().getHeartbeat());
        session.getRemote().sendString(gson.toJson(init));
        apiSession.setSession(session);
        apiSession.updateLastBeat();
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {

    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        ApiSession apiSession = ApiManager.getInstance().getSessionFromWebsocket(session);
        if (apiSession == null) {
            session.close();
            return;
        }
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        String command = json.get("command").getAsString();
        if (command.equals("heartbeat")) {
            apiSession.updateLastBeat();
        }

    }
}
