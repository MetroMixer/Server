package com.weeryan17.mixer.server.models.web;

import com.google.gson.Gson;

public class WebHandlerObjects {

    private final Gson gson;

    public WebHandlerObjects(Gson gson) {
        this.gson = gson;
    }

    public Gson getGson() {
        return gson;
    }

}
