package com.weeryan17.mixer.server.commands;

import com.weeryan17.mixer.server.commandmeta.Command;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.rest.MixerWebSocket;
import com.weeryan17.mixer.shared.command.data.CreateChannels;
import com.weeryan17.mixer.shared.command.meta.CommandType;
import com.weeryan17.mixer.shared.models.ChannelInfo;
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
            try {
                mixerWebSocket.sendCommand(session, CommandType.CREATE_CHANNELS, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
