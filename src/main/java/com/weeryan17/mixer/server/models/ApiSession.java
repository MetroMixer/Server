package com.weeryan17.mixer.server.models;

import org.eclipse.jetty.websocket.api.Session;

public class ApiSession {

    private String key;
    private Session session;
    private long lastBeatTime = -1;

    public ApiSession(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }

    public void updateLastBeat() {
        lastBeatTime = System.currentTimeMillis();
    }

    public void disconnect() {
        session.close();
        //TODO implement
    }
}
