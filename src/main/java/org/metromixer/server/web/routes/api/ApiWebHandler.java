package org.metromixer.server.web.routes.api;

import org.metromixer.server.MixerServer;
import org.metromixer.server.models.exceptions.AuthException;
import org.metromixer.server.models.managers.ApiManager;
import org.metromixer.server.models.web.WebHandlerObjects;
import org.metromixer.server.models.web.api.Connect;
import org.metromixer.server.models.web.api.ConnectionApproved;
import org.metromixer.server.utils.RandomUtils;
import org.metromixer.server.web.WebHandler;
import org.metromixer.server.web.WebRoot;

import static io.javalin.apibuilder.ApiBuilder.*;

@WebRoot
public class ApiWebHandler extends WebHandler {
    public ApiWebHandler(WebHandlerObjects objects) {
        super(objects);
    }

    @Override
    public void init() {
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
            ApiManager.getInstance().createSession(key);

            ctx.json(connectionApproved);
            ctx.status(200);
        });
    }
}
