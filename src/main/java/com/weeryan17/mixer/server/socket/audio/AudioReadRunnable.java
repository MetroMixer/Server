package com.weeryan17.mixer.server.socket.audio;

import com.weeryan17.mixer.server.utils.ThreadExecutorContainer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AudioReadRunnable implements Runnable {

    private ThreadExecutorContainer threadExecutorContainer;
    private Socket socket;
    public AudioReadRunnable(ThreadExecutorContainer threadExecutorContainer, Socket socket) {
        this.threadExecutorContainer = threadExecutorContainer;
        this.socket = socket;
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
                ByteBuffer lenBuf = ByteBuffer.wrap(in.readNBytes(4));
                int len = lenBuf.getInt();
                byte[] rawData = in.readNBytes(len);
                ByteBuffer buffer = ByteBuffer.wrap(rawData);
                int keyLen = buffer.getInt();
                byte[] keyBytes = Arrays.copyOfRange(rawData, 4, 4 + keyLen);
                String key = new String(keyBytes, StandardCharsets.UTF_8);
                byte[] data = Arrays.copyOfRange(rawData, 4 + keyLen, rawData.length);
                threadExecutorContainer.queueThread(new AudioProcessRunnable(data, key));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
