package com.weeryan17.mixer.server.models.web.api;

public class ConnectionApproved {

    private String key;

    public ConnectionApproved(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
