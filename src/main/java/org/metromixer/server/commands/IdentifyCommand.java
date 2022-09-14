package org.metromixer.server.commands;

import org.metromixer.server.MixerServer;
import org.metromixer.server.commandmeta.Command;
import org.metromixer.server.models.Client;
import org.metromixer.server.models.PendingContainer;
import org.metromixer.server.models.managers.ClientManager;
import org.metromixer.server.web.routes.mixer.MixerWebSocket;
import org.metromixer.server.utils.RandomUtils;
import org.metromixer.shared.command.data.IdentifyProperties;
import org.metromixer.shared.command.data.Init;
import org.metromixer.shared.command.meta.CommandType;
import org.eclipse.jetty.websocket.api.Session;
import org.jaudiolibs.jnajack.JackException;

import java.io.IOException;

public class IdentifyCommand implements Command<IdentifyProperties> {

    @Override
    public void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, IdentifyProperties data) {
        if (client == null) {
            PendingContainer pendingContainer = new PendingContainer(data, accepted -> {
                if (!accepted) {
                    return null;
                }
                String key = RandomUtils.getInstance().randomKey();
                Client acceptedClient = null;
                try {
                    acceptedClient = ClientManager.getInstance().buildClient(key, data);
                } catch (JackException e) {
                    e.printStackTrace();
                    return e;
                }
                acceptedClient.setSession(session);
                acceptedClient.updateLastBeatTime();
                Init init = new Init(ClientManager.getInstance().getHeartbeat(), MixerServer.getInstance().getAudioUdpPort(), MixerServer.getInstance().getConfig().shouldCompress(), key);
                try {
                    mixerWebSocket.sendCommand(session, CommandType.INIT, init);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            });
            ClientManager.getInstance().getPendingClients().add(pendingContainer);
        } else {
            Init init = new Init(ClientManager.getInstance().getHeartbeat(), MixerServer.getInstance().getAudioUdpPort(), MixerServer.getInstance().getConfig().shouldCompress());
            try {
                mixerWebSocket.sendCommand(session, CommandType.INIT, init);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
