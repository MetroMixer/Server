package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.PendingContainer;
import com.weeryan17.mixer.server.models.managers.ApiManager;
import com.weeryan17.mixer.server.models.managers.ClientManager;
import com.weeryan17.mixer.server.utils.RandomUtils;

import java.util.stream.Collectors;

import static spark.Spark.*;

public class WebController {

    private Gson gson;
    public WebController(Gson gson) {
        this.gson = gson;
    }

    private String password;

    public int initController() {
        port(MixerServer.getInstance().getConfig().getTcpPort());
        if (MixerServer.getInstance().getConfig().getApiListen() != null) {
            ipAddress(MixerServer.getInstance().getConfig().getApiListen());
        }
        int port = port();
        password = MixerServer.getInstance().getConfig().getApiPassword();
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
                String auth = req.headers("Auth");
                if (auth == null) { //TODO check auth
                    JsonObject invalid = new JsonObject();
                    invalid.addProperty("error", "Key required to access this endpoint");
                    halt(403, gson.toJson(invalid));
                }
            }
        });
        post("/connect", (req, res) -> {
            if (password != null) {
                JsonObject jsonObject = JsonParser.parseString(req.body()).getAsJsonObject();
                String testPassword = jsonObject.get("password").getAsString();
                if (!testPassword.equals(password)) {
                    JsonObject invalid = new JsonObject();
                    invalid.addProperty("error", "Invalid password");
                    res.status(403);
                    return gson.toJson(invalid);
                }
            }
            String key = RandomUtils.getInstance().randomKey();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("key", key);
            ApiManager.getInstance().createSession(key);
            return gson.toJson(jsonObject);
        });
        path("/devices", () -> {
            get("/", (req, res) -> {
                return gson.toJson(ClientManager.getInstance().getClientList());
            });
            post("/add", (req, res) -> {
                return "";
            });
            get("/pending", (req, res) -> {
                return gson.toJson(ClientManager.getInstance().getPendingClients().stream().map(PendingContainer::getIdentifyProperties).collect(Collectors.toList()));
            });
            post("/accept", (req, res) -> {
                JsonObject object = JsonParser.parseString(req.body()).getAsJsonObject();
                String id = object.get("client").getAsJsonObject().get("id").getAsString();
                PendingContainer container = ClientManager.getInstance().getPendingClients().stream()
                        .filter(pendingContainer -> pendingContainer.getIdentifyProperties().getId().equals(id)).findFirst().orElse(null);

                if (container == null) {
                    JsonObject invalid = new JsonObject();
                    invalid.addProperty("error", "Key required to access this endpoint");
                    res.status(401);
                    return gson.toJson(invalid);
                }

                Client client = ClientManager.getInstance().buildClient(RandomUtils.getInstance().randomKey(), container.getIdentifyProperties());
                return gson.toJson(client);
            });
            delete("/disconnect", (req, res) -> {
                return "";
            });
        });
    }

}
