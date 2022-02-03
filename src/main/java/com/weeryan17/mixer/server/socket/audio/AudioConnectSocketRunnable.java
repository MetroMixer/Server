package com.weeryan17.mixer.server.socket.audio;

import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.managers.ClientManager;
import com.weeryan17.mixer.server.utils.ThreadExecutorContainer;
import com.weeryan17.rudp.ReliableServerSocket;
import com.weeryan17.rudp.ReliableSocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class AudioConnectSocketRunnable implements Runnable {

    private final ThreadExecutorContainer connectionContainer;
    private final ThreadExecutorContainer processContainer;

    private ReliableServerSocket reliableServerSocket;
    public AudioConnectSocketRunnable(ReliableServerSocket reliableServerSocket) {
        connectionContainer = new ThreadExecutorContainer("AudioConnections", MixerServer.getInstance().getConfig().getMaxConnectThreads());
        processContainer = new ThreadExecutorContainer("AudioSocketProcess", MixerServer.getInstance().getConfig().getMaxReceiveThreads());
        this.reliableServerSocket = reliableServerSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ReliableSocket socket = (ReliableSocket) reliableServerSocket.accept();
                InputStream in = socket.getInputStream();
                int length = ByteBuffer.wrap(in.readNBytes(4)).getInt();
                String key = new String(in.readNBytes(length), StandardCharsets.UTF_8);
                Client client = ClientManager.getInstance().getClientWithKey(key);
                if (client == null) {
                    socket.close();
                    return;
                }
                client.setSocket(socket);
                connectionContainer.queueThread(new AudioReadRunnable(processContainer, socket, key));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
