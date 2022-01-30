package com.weeryan17.mixer.server.socket.audio;

import com.weeryan17.mixer.server.utils.ThreadExecutorContainer;
import com.weeryan17.rudp.ReliableServerSocket;
import com.weeryan17.rudp.ReliableSocket;

import java.io.IOException;

public class AudioConnectSocketRunnable implements Runnable {

    private final ThreadExecutorContainer connectionContainer = new ThreadExecutorContainer("AudioConnections");
    private final ThreadExecutorContainer processContainer = new ThreadExecutorContainer("AudioSocketProcess");

    private ReliableServerSocket reliableServerSocket;
    public AudioConnectSocketRunnable(ReliableServerSocket reliableServerSocket) {
        this.reliableServerSocket = reliableServerSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ReliableSocket socket = (ReliableSocket) reliableServerSocket.accept();
                connectionContainer.queueThread(new AudioReadRunnable(processContainer, socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
