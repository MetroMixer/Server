package com.weeryan17.mixer.server.socket.audio;

import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.models.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AudioSendRunnable implements Runnable {

    private Client client;
    private byte[] audio;

    public AudioSendRunnable(Client client, byte[] audio) {
        this.client = client;
        this.audio = audio;
    }

    @Override
    public void run() {
        byte[] audio = this.audio;
        if (MixerServer.getInstance().getConfig().shouldCompress()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
                gzipOutputStream.write(audio);
                audio = out.toByteArray();
            } catch (IOException ignored) {}
        }
        try {
            client.getSocket().getOutputStream().write(audio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
