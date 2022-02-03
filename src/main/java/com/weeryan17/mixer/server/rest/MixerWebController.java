package com.weeryan17.mixer.server.rest;

import com.google.gson.Gson;
import com.weeryan17.mixer.server.MixerServer;

import static spark.Spark.*;

public class MixerWebController {

    private Gson gson;
    public MixerWebController(Gson gson) {
        this.gson = gson;
    }

    public int initController() {
        port(MixerServer.getInstance().getConfig().getAudioTcpPort());
        if (MixerServer.getInstance().getConfig().getAudioListen() != null) {
            ipAddress(MixerServer.getInstance().getConfig().getAudioListen());
        }
        int port = port();
        createWsRoutes();
        return port;
    }

    public void createWsRoutes() {
        webSocket("/", new MixerWebSocket(gson));
    }

}
