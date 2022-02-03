package com.weeryan17.mixer.server.commandmeta;

import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.rest.MixerWebSocket;
import com.weeryan17.mixer.shared.command.meta.CommandData;
import org.eclipse.jetty.websocket.api.Session;

public interface Command<T extends CommandData> {

    void runCommand(MixerWebSocket mixerWebSocket, Session session, Client client, T data);

}
