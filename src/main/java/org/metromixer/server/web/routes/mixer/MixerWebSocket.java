package org.metromixer.server.web.routes.mixer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.metromixer.server.MixerServer;
import org.metromixer.server.commandmeta.CommandList;
import org.metromixer.server.models.Client;
import org.metromixer.server.models.managers.ClientManager;
import org.metromixer.server.models.web.WebHandlerObjects;
import org.metromixer.server.web.WebHandler;
import org.metromixer.server.web.WebRoot;
import org.metromixer.shared.command.data.Init;
import org.metromixer.shared.command.data.Invalid;
import org.metromixer.shared.command.data.VersionProperties;
import org.metromixer.shared.command.meta.CommandData;
import org.metromixer.shared.command.meta.CommandType;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static io.javalin.apibuilder.ApiBuilder.ws;

@WebRoot
public class MixerWebSocket extends WebHandler {


    public MixerWebSocket(WebHandlerObjects objects) {
        super(objects);
    }

    public void sendCommand(Session session, CommandType command, CommandData data) throws IOException {
        if (!command.getJavaClass().equals(data.getClass())) {
            throw new IllegalArgumentException("Data type does not match command type");
        }
        JsonObject send = new JsonObject();
        send.addProperty("command", command.getCommand());
        send.add("data", getObjects().getGson().toJsonTree(data));
        session.getRemote().sendString(getObjects().getGson().toJson(send));
    }

    @Override
    public void init() {
        ws(wsConfig -> {
            wsConfig.onConnect(wsConnectContext -> {
                Session session = wsConnectContext.session;
                String key = session.getUpgradeRequest().getHeader("key");
                Client client = ClientManager.getInstance().getClientWithKey(key);
                sendCommand(session, CommandType.VERSION, new VersionProperties(MixerServer.getInstance().getServerVersion(), MixerServer.getInstance().getAudioVersion(), MixerServer.getInstance().getApiVersion()));
                if (client == null && key != null) {
                    sendCommand(session, CommandType.INVALID, new Invalid(1, "Key is not valid", false));
                }
                if (client != null) {
                    sendCommand(session, CommandType.INIT, new Init(ClientManager.getInstance().getHeartbeat(), MixerServer.getInstance().getAudioUdpPort(), MixerServer.getInstance().getConfig().shouldCompress()));
                    client.setSession(session);
                    client.updateLastBeatTime();
                }
            });
            wsConfig.onClose(wsCloseContext -> {
                Session session = wsCloseContext.session;
                Client client = ClientManager.getInstance().getClientWithSession(session);
                if (client != null) {
                    client.shutdown();
                }
            });
            wsConfig.onMessage(wsMessageContext -> {
                Session session = wsMessageContext.session;
                String message = wsMessageContext.message();

                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                String commandStr = jsonObject.get("command").getAsString();

                Client client = ClientManager.getInstance().getClientWithSession(session);
                if (client == null && !commandStr.equals("identify")) {
                    session.close();
                    return;
                }

                if (commandStr.equals("heartbeat")) {
                    client.updateLastBeatTime();
                    return;
                }

                CommandData commandData = getObjects().getGson().fromJson(jsonObject.get("data").getAsJsonObject(), CommandType.getByCommand(commandStr).getJavaClass());

                try {
                    CommandList.getByCommand(commandStr).createCommand().runCommand(this, session, client, commandData);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
