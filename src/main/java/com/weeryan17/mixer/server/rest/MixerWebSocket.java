package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.builder.ClientManager;
import com.weeryan17.mixer.shared.command.data.VersionProperties;
import com.weeryan17.mixer.shared.command.meta.CommandData;
import com.weeryan17.mixer.shared.command.meta.CommandType;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class MixerWebSocket {

    private Gson gson;
    public MixerWebSocket(Gson gson) {
        this.gson = gson;
    }

    @OnWebSocketConnect
    public void connected(Session session) throws IOException {
        String key = session.getUpgradeRequest().getHeader("key");
        if (key == null) {
            session.close();
            return;
        }
        Client client = ClientManager.getInstance().getClientWithKey(key);
        if (client == null) {
            session.close();
            return;
        }
        sendCommand(session, "version", new VersionProperties(MixerServer.getInstance().getServerVersion(), MixerServer.getInstance().getAudioVersion(), MixerServer.getInstance().getApiVersion()));
        client.setSession(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        Client client = ClientManager.getInstance().getClientWithSession(session);
        client.shutdown();
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        String commandStr = jsonObject.get("command").getAsString();
        CommandData commandData = gson.fromJson(jsonObject.get("data").getAsJsonObject(), CommandType.getByCommand(commandStr).getJavaClass());

        Client client = ClientManager.getInstance().getClientWithSession(session);
        if (client.isPendingApproval() && !commandStr.equals("identify")) {
            return;
        }
    }

    public void sendCommand(Session session, String command, CommandData data) throws IOException {
        JsonObject send = new JsonObject();
        send.addProperty("command", command);
        send.add("data", gson.toJsonTree(data));
        session.getRemote().sendString(gson.toJson(send));
    }

}
