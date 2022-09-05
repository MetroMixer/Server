package com.weeryan17.mixer.server.web.routes.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weeryan17.mixer.server.models.ApiSession;
import com.weeryan17.mixer.server.models.managers.ApiManager;
import com.weeryan17.mixer.server.models.web.WebHandlerObjects;
import com.weeryan17.mixer.server.web.WebHandler;
import com.weeryan17.mixer.server.web.WebRoot;

import java.io.IOException;

import static io.javalin.apibuilder.ApiBuilder.*;

@WebRoot
public class ApiWebSocket extends WebHandler {

    public ApiWebSocket(WebHandlerObjects objects) {
        super(objects);
    }

    @Override
    public void init() {
        ws(wsConfig -> {
            wsConfig.onConnect(wsConnectContext -> {
                String key = wsConnectContext.header("key");
                ApiSession apiSession = ApiManager.getInstance().getSessionFromKey(key);
                if (apiSession == null) {
                    wsConnectContext.session.close();
                    return;
                }
                JsonObject init = new JsonObject();
                init.addProperty("beat", ApiManager.getInstance().getHeartbeat());
                wsConnectContext.session.getRemote().sendString(getObjects().getGson().toJson(init));
                apiSession.setSession(wsConnectContext.session);
                apiSession.updateLastBeat();
            });
            wsConfig.onMessage(wsMessageContext -> {
                ApiSession apiSession = ApiManager.getInstance().getSessionFromWebsocket(wsMessageContext.session);
                if (apiSession == null) {
                    wsMessageContext.session.close();
                    return;
                }
                JsonObject json = JsonParser.parseString(wsMessageContext.message()).getAsJsonObject();
                String command = json.get("command").getAsString();
                if (command.equals("heartbeat")) {
                    apiSession.updateLastBeat();
                }
            });
        });
    }
}
