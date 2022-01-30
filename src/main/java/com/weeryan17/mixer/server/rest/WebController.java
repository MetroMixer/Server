package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;

import static spark.Spark.*;

public class WebController {

    private Gson gson;
    public WebController(Gson gson) {
        this.gson = gson;
    }

    public int initController() {
        port(0);
        int port = port();
        createWsRoutes();
        createRoutes();
        return port;
    }

    private void createWsRoutes() {
        webSocket("/audio", new MixerWebSocket(gson));
        webSocket("/api", new ApiWebSocket(gson));
    }

    private void createRoutes() {
        post("/connect", (req, res) -> {
            return "";
        });
        path("/devices", () -> {
            get("/", (req, res) -> {
                return "";
            });
            post("/add", (req, res) -> {
                return "";
            });
            get("/pending", (req, res) -> {
                return "";
            });
            post("/accept", (req, res) -> {
                return "";
            });
            delete("/disconnect", (req, res) -> {
                return "";
            });
        });
    }

}
