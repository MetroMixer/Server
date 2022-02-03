package com.weeryan17.mixer.server.commands;

import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.commandmeta.Command;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.PendingContainer;
import com.weeryan17.mixer.server.models.builder.ClientManager;
import com.weeryan17.mixer.server.rest.MixerWebSocket;
import com.weeryan17.mixer.server.utils.RandomUtils;
import com.weeryan17.mixer.shared.command.data.IdentifyProperties;
import com.weeryan17.mixer.shared.command.data.Init;
import com.weeryan17.mixer.shared.command.meta.CommandType;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class IdentifyCommand implements Command<IdentifyProperties> {

    @Override
    public void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, IdentifyProperties data) {
        if (client == null) {
            PendingContainer pendingContainer = new PendingContainer(data, accepted -> {
                String key = RandomUtils.getInstance().randomKey();
                Client acceptedClient = ClientManager.getInstance().buildClient(key, data);
                acceptedClient.setSession(session);
                acceptedClient.updateLastBeatTime();
                Init init = new Init(ClientManager.getInstance().getHeartbeat(), MixerServer.getInstance().getAudioUdpPort(), key);
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
            Init init = new Init(ClientManager.getInstance().getHeartbeat(), MixerServer.getInstance().getAudioUdpPort());
            try {
                mixerWebSocket.sendCommand(session, CommandType.INIT, init);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
