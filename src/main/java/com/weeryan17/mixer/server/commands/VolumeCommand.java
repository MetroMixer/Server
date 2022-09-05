package com.weeryan17.mixer.server.commands;

import com.weeryan17.mixer.server.commandmeta.Command;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.web.routes.mixer.MixerWebSocket;
import com.weeryan17.mixer.shared.command.data.Volume;
import org.eclipse.jetty.websocket.api.Session;

public class VolumeCommand implements Command<Volume> {
    @Override
    public void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, Volume data) {

    }
}
