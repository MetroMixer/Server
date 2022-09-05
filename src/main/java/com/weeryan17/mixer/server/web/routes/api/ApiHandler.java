package com.weeryan17.mixer.server.web.routes.api;

import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.models.ApiSession;
import com.weeryan17.mixer.server.models.exceptions.AuthException;
import com.weeryan17.mixer.server.models.managers.ApiManager;
import com.weeryan17.mixer.server.models.web.WebHandlerObjects;
import com.weeryan17.mixer.server.models.web.api.Connect;
import com.weeryan17.mixer.server.models.web.api.ConnectionApproved;
import com.weeryan17.mixer.server.utils.RandomUtils;
import com.weeryan17.mixer.server.web.WebHandler;
import com.weeryan17.mixer.server.web.WebRoot;

import static io.javalin.apibuilder.ApiBuilder.*;

@WebRoot
public class ApiHandler extends WebHandler {
    public ApiHandler(WebHandlerObjects objects) {
        super(objects);
    }

    @Override
    public void init() {
        before(ctx -> {
            if (ctx.path().equals("/connect")) {
                return;
            }
            String auth = ctx.header("Auth");
            if (auth == null) {
                throw new AuthException("Header not provided");
            }

            ApiSession session = ApiManager.getInstance().getSessionFromKey(auth);
            if (session == null) {
                throw new AuthException("Key is invalid");
            }
        });
        post("/connect", ctx -> {
            String password = MixerServer.getInstance().getConfig().getApiPassword();
            if (password != null) {
                Connect connect = ctx.bodyAsClass(Connect.class);
                if (connect == null || connect.getPassword() == null) {
                    throw new AuthException("Password required");
                }

                if (!connect.getPassword().equals(password)) {
                    throw new AuthException("Password invalid");
                }
            }
            String key = RandomUtils.getInstance().randomKey();
            ConnectionApproved connectionApproved = new ConnectionApproved(key);
            ctx.json(connectionApproved);
            ctx.status(200);
        });
    }
}
