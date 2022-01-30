package com.weeryan17.mixer.server.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.utils.RandomUtils;
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
    private String id;
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

    public String getId() {
        return id;
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

    public void connect(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        key = RandomUtils.getInstance().randomKey();
        id = RandomUtils.getInstance().randomString(4);
        String thisAddress = socket.getLocalAddress().getHostAddress();
        JsonObject toSend = new JsonObject();
        toSend.addProperty("ip", thisAddress);
        toSend.addProperty("port", MixerServer.getInstance().getTcpPort());
        toSend.addProperty("key", key);
        socket.getOutputStream().write(gson.toJson(toSend).getBytes(StandardCharsets.UTF_8));
        socket.close();
    }

    public void shutdown() {
        session = null;

    }

}
