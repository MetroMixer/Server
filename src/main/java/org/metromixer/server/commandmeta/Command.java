package org.metromixer.server.commandmeta;

import org.metromixer.server.models.Client;
import org.metromixer.server.web.routes.mixer.MixerWebSocket;
import org.metromixer.shared.command.meta.CommandData;
import org.eclipse.jetty.websocket.api.Session;

public interface Command<T extends CommandData> {

    void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, T data);

}
