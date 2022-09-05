package org.metromixer.server.commands;

import org.metromixer.server.commandmeta.Command;
import org.metromixer.server.models.Client;
import org.metromixer.server.web.routes.mixer.MixerWebSocket;
import org.metromixer.shared.command.data.Volume;
import org.eclipse.jetty.websocket.api.Session;

public class VolumeCommand implements Command<Volume> {
    @Override
    public void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, Volume data) {

    }
}
