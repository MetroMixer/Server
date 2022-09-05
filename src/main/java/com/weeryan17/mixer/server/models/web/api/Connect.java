package com.weeryan17.mixer.server.models.web.api;

public class Connect {

    private String password;

    public Connect(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
