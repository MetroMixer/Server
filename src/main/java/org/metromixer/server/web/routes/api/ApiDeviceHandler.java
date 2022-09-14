package org.metromixer.server.web.routes.api;

import org.metromixer.server.models.Client;
import org.metromixer.server.models.PendingContainer;
import org.metromixer.server.models.exceptions.AuthException;
import org.metromixer.server.models.exceptions.DeviceDoesNotExistException;
import org.metromixer.server.models.managers.ClientManager;
import org.metromixer.server.models.web.WebHandlerObjects;
import org.metromixer.server.models.web.api.device.ApproveDevice;
import org.metromixer.server.models.web.api.device.SimpleClient;
import org.metromixer.server.web.WebHandler;
import org.metromixer.server.web.WebRoot;
import org.metromixer.shared.command.data.IdentifyProperties;

import java.util.stream.Collectors;

import static io.javalin.apibuilder.ApiBuilder.*;

@WebRoot(url = "/devices")
public class ApiDeviceHandler extends WebHandler {

    public ApiDeviceHandler(WebHandlerObjects objects) {
        super(objects);
    }

    @Override
    public void init() {
        get(ctx -> {
            ctx.json(ClientManager.getInstance().getClientList());
            ctx.status(200);
        });
        post("/add", ctx -> {
            throw new RuntimeException("Not yet implemented");
        });
        get("/pending", ctx -> {
            ctx.json(ClientManager.getInstance().getPendingClients().stream().map(PendingContainer::getIdentifyProperties).collect(Collectors.toList()));
            ctx.status(200);
        });
        post("/accept", ctx -> {
            ApproveDevice device = ctx.bodyAsClass(ApproveDevice.class);
            PendingContainer container = ClientManager.getInstance().getPendingClients().stream()
                    .filter(pendingContainer -> pendingContainer.getIdentifyProperties().getId().equals(device.getDeviceId())).findFirst().orElse(null);

            if (container == null) {
                throw new DeviceDoesNotExistException("The specified device is not pending approval, or does not exist!", device.getDeviceId());
            }

            container.getAcceptConsumer().apply(true);
            ctx.status(200);
        });
        delete(ctx -> {
            throw new RuntimeException("Not yet implemented");
        });
    }
}
