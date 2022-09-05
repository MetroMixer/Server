package org.metromixer.server.web.routes.api;

import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import org.metromixer.server.MixerServer;
import org.metromixer.server.models.ApiSession;
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
public class ApiHandler extends WebHandler {
    public ApiHandler(WebHandlerObjects objects) {
        super(objects);
    }

    @Override
    public void init() {
        post("/connect", OpenApiBuilder.documented(OpenApiBuilder.document().body(Connect.class).json("200", ConnectionApproved.class), ctx -> {
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
        }));
    }
}
