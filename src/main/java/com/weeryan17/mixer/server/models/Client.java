package com.weeryan17.mixer.server.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.rest.MixerWebSocket;
import com.weeryan17.mixer.server.utils.RandomUtils;
import com.weeryan17.mixer.shared.command.data.IdentifyProperties;
import com.weeryan17.mixer.shared.command.data.Init;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    private Gson gson;

    private long beatInterval;
    public Client(Gson gson, long beatInterval) {
        this.gson = gson;
        this.beatInterval = beatInterval;
    }

    public long getBeatInterval() {
        return beatInterval;
    }

    private Session session;
    private String key;
    private long lastBeatTime = -1;

    private boolean pendingApproval = true;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getKey() {
        return key;
    }

    public long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }

    public boolean isPendingApproval() {
        return pendingApproval;
    }

    public void setPendingApproval(boolean pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public void updateLastBeatTime() {
        lastBeatTime = System.currentTimeMillis();
    }

    public void accept(MixerWebSocket webSocket, Session session, int port) throws IOException {
        //TODO key
        Init init = new Init(beatInterval, port);
        webSocket.sendCommand(session, "init", init);
    }

    public void shutdown() {
        session = null;

    }

}
