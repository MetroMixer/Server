package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.commandmeta.CommandList;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.managers.ClientManager;
import com.weeryan17.mixer.shared.command.data.Init;
import com.weeryan17.mixer.shared.command.data.Invalid;
import com.weeryan17.mixer.shared.command.data.VersionProperties;
import com.weeryan17.mixer.shared.command.meta.CommandData;
import com.weeryan17.mixer.shared.command.meta.CommandType;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@WebSocket
public class MixerWebSocket {

    private Gson gson;
    public MixerWebSocket(Gson gson) {
        this.gson = gson;
    }

    @OnWebSocketConnect
    public void connected(Session session) throws IOException {
        String key = session.getUpgradeRequest().getHeader("key");
        Client client = ClientManager.getInstance().getClientWithKey(key);
        sendCommand(session, CommandType.VERSION, new VersionProperties(MixerServer.getInstance().getServerVersion(), MixerServer.getInstance().getAudioVersion(), MixerServer.getInstance().getApiVersion()));
        if (client == null && key != null) {
            sendCommand(session, CommandType.INVALID, new Invalid(1, "Key is not valid", false));
        }
        if (client != null) {
            sendCommand(session, CommandType.INIT, new Init(ClientManager.getInstance().getHeartbeat(), MixerServer.getInstance().getAudioUdpPort()));
            client.setSession(session);
            client.updateLastBeatTime();
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        Client client = ClientManager.getInstance().getClientWithSession(session);
        if (client != null) {
            client.shutdown();
        }
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        String commandStr = jsonObject.get("command").getAsString();

        Client client = ClientManager.getInstance().getClientWithSession(session);
        if (client == null && !commandStr.equals("identify")) {
            session.close();
            return;
        }

        if (commandStr.equals("heartbeat")) {
            client.updateLastBeatTime();
            return;
        }

        CommandData commandData = gson.fromJson(jsonObject.get("data").getAsJsonObject(), CommandType.getByCommand(commandStr).getJavaClass());

        try {
            CommandList.getByCommand(commandStr).createCommand().runCommand(this, session, client, commandData);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(Session session, CommandType command, CommandData data) throws IOException {
        if (!command.getJavaClass().equals(data.getClass())) {
            throw new IllegalArgumentException("Data type does not match command type");
        }
        JsonObject send = new JsonObject();
        send.addProperty("command", command.getCommand());
        send.add("data", gson.toJsonTree(data));
        session.getRemote().sendString(gson.toJson(send));
    }

}
