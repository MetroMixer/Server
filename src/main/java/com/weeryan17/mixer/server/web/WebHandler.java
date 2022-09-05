package com.weeryan17.mixer.server.web;

import com.weeryan17.mixer.server.models.web.WebHandlerObjects;

public abstract class WebHandler {

    private final WebHandlerObjects objects;

    public WebHandler(WebHandlerObjects objects) {
        this.objects = objects;
    }

    protected WebHandlerObjects getObjects() {
        return objects;
    }

    public abstract void init();

}
