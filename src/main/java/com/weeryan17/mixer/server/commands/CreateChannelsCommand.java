package com.weeryan17.mixer.server.commands;

import com.weeryan17.mixer.server.commandmeta.Command;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.rest.MixerWebSocket;
import com.weeryan17.mixer.shared.command.data.CreateChannels;
import org.eclipse.jetty.websocket.api.Session;

public class CreateChannelsCommand implements Command<CreateChannels> {

    @Override
    public void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, CreateChannels data) {

    }

}
