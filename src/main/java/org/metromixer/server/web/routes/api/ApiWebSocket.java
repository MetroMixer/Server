package org.metromixer.server.web.routes.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.metromixer.server.models.ApiSession;
import org.metromixer.server.models.managers.ApiManager;
import org.metromixer.server.models.web.WebHandlerObjects;
import org.metromixer.server.web.WebHandler;
import org.metromixer.server.web.WebRoot;

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
