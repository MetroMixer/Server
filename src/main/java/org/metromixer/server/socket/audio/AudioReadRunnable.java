package org.metromixer.server.socket.audio;

import org.metromixer.server.utils.ThreadExecutorContainer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class AudioReadRunnable implements Runnable {

    private ThreadExecutorContainer threadExecutorContainer;
    private Socket socket;
    private String key;
    public AudioReadRunnable(ThreadExecutorContainer threadExecutorContainer, Socket socket, String key) {
        this.threadExecutorContainer = threadExecutorContainer;
        this.socket = socket;
        this.key = key;
    }

    @Override
    public void run() {
        InputStream in;
        try {
            in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            try {
                int len = ByteBuffer.wrap(in.readNBytes(4)).getInt();
                byte[] data = in.readNBytes(len);
                threadExecutorContainer.queueThread(new AudioProcessRunnable(data, key, System.currentTimeMillis()));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
