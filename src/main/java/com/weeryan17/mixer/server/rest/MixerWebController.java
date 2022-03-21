package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import com.weeryan17.mixer.server.MixerServer;
import spark.Service;

public class MixerWebController {

    private Gson gson;
    public MixerWebController(Gson gson) {
        this.gson = gson;
    }

    public int initController() {
        Service http = Service.ignite();
        http.port(MixerServer.getInstance().getConfig().getAudioTcpPort());
        if (MixerServer.getInstance().getConfig().getAudioListen() != null) {
            http.ipAddress(MixerServer.getInstance().getConfig().getAudioListen());
        }
        createWsRoutes(http);
        http.get("/dummy", (req, res) -> {
            return "";
        });
        http.init();
        http.awaitInitialization();
        /*while (http.port() == 0) {

        }*/
        return http.port();
    }

    public void createWsRoutes(Service http) {
        http.webSocket("/", new MixerWebSocket(gson));
    }

}
