package org.metromixer.server.web;

import org.metromixer.server.models.web.WebHandlerObjects;

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
