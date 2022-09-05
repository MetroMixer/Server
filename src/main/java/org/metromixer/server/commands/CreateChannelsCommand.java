package org.metromixer.server.commands;

import org.metromixer.server.commandmeta.Command;
import org.metromixer.server.models.Client;
import org.metromixer.server.web.routes.mixer.MixerWebSocket;
import org.metromixer.shared.command.data.CreateChannels;
import org.metromixer.shared.command.meta.CommandType;
import org.metromixer.shared.models.ChannelInfo;
import org.eclipse.jetty.websocket.api.Session;
import org.jaudiolibs.jnajack.JackException;

import java.io.IOException;

public class CreateChannelsCommand implements Command<CreateChannels> {

    @Override
    public void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, CreateChannels data) {
        for (ChannelInfo channelInfo : data.getChannels()) {
            try {
                int id = client.createChannel(channelInfo.getName(), channelInfo.getType());
                channelInfo.setId(id);
            } catch (JackException e) {
                e.printStackTrace();
            }
        }
        try {
            mixerWebSocket.sendCommand(session, CommandType.CREATE_CHANNELS, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
