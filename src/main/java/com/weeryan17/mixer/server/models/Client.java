package com.weeryan17.mixer.server.models;

import com.google.gson.Gson;
import com.weeryan17.mixer.shared.command.data.IdentifyProperties;
import org.eclipse.jetty.websocket.api.Session;
import org.jaudiolibs.jnajack.JackClient;

public class Client {

    private Gson gson;

    private JackClient jackClient;

    private IdentifyProperties id;

    private long beatInterval;
    public Client(Gson gson, IdentifyProperties id, long beatInterval) {
        this.gson = gson;
        this.beatInterval = beatInterval;
        this.id = id;
    }

    public long getBeatInterval() {
        return beatInterval;
    }

    private Session session;
    private String key;
    private long lastBeatTime = -1;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }

    public void updateLastBeatTime() {
        lastBeatTime = System.currentTimeMillis();
    }

    public void shutdown() {
        session = null;

    }

}
