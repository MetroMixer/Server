package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.models.PendingContainer;
import com.weeryan17.mixer.server.models.builder.ClientManager;

import java.util.stream.Collectors;

import static spark.Spark.*;

public class WebController {

    private Gson gson;
    public WebController(Gson gson) {
        this.gson = gson;
    }

    public int initController() {
        port(MixerServer.getInstance().getConfig().getTcpPort());
        if (MixerServer.getInstance().getConfig().getApiListen() != null) {
            ipAddress(MixerServer.getInstance().getConfig().getApiListen());
        }
        int port = port();
        createWsRoutes();
        createRoutes();
        return port;
    }

    private void createWsRoutes() {
        webSocket("/api", new ApiWebSocket(gson));
    }

    private void createRoutes() {
        before((req, res) -> {
            if (!req.pathInfo().equals("/connect")) {
                String auth = req.headers("Auth"); //TODO check auth
                if (auth == null) {
                    JsonObject invalid = new JsonObject();
                    invalid.addProperty("error", "Key required to access this endpoint");
                    halt(403, gson.toJson(invalid));
                }
            }
        });
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
                return gson.toJson(ClientManager.getInstance().getPendingClients().stream().map(PendingContainer::getIdentifyProperties).collect(Collectors.toList()));
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
