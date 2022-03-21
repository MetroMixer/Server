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
import spark.Service;

import java.util.stream.Collectors;

public class WebController {

    private Gson gson;
    public WebController(Gson gson) {
        this.gson = gson;
    }

    private String password;

    public int initController() {
        Service http = Service.ignite();
        http.port(MixerServer.getInstance().getConfig().getTcpPort());
        if (MixerServer.getInstance().getConfig().getApiListen() != null) {
            http.ipAddress(MixerServer.getInstance().getConfig().getApiListen());
        }
        password = MixerServer.getInstance().getConfig().getApiPassword();
        createWsRoutes(http);
        createRoutes(http);
        http.init();
        http.awaitInitialization();
        /*while (http.port() == 0) {

        }*/
        return http.port();
    }

    private void createWsRoutes(Service http) {
        http.webSocket("/api", new ApiWebSocket(gson));
    }

    private void createRoutes(Service http) {
        http.before((req, res) -> {
            if (!req.pathInfo().equals("/connect")) {
                String auth = req.headers("Auth");
                if (auth == null) { //TODO check auth
                    JsonObject invalid = new JsonObject();
                    invalid.addProperty("error", "Key required to access this endpoint");
                    http.halt(403, gson.toJson(invalid));
                }
            }
        });
        http.post("/connect", (req, res) -> {
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
        http.path("/devices", () -> {
            http.get("/", (req, res) -> {
                return gson.toJson(ClientManager.getInstance().getClientList());
            });
            http.post("/add", (req, res) -> {
                return "";
            });
            http.get("/pending", (req, res) -> {
                return gson.toJson(ClientManager.getInstance().getPendingClients().stream().map(PendingContainer::getIdentifyProperties).collect(Collectors.toList()));
            });
            http.post("/accept", (req, res) -> {
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

                container.getAcceptConsumer().apply(true);

                //Client client = ClientManager.getInstance().buildClient(RandomUtils.getInstance().randomKey(), container.getIdentifyProperties());
                return "true";
            });
            http.delete("/disconnect", (req, res) -> {
                return "";
            });
        });
    }

}
