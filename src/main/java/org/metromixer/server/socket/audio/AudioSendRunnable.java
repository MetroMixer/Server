package org.metromixer.server.socket.audio;

import org.metromixer.server.MixerServer;
import org.metromixer.server.models.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
                gzipOutputStream.flush();
                gzipOutputStream.close();
                audio = out.toByteArray();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(audio.length);
            client.getSocket().getOutputStream().write(buffer.array());
            client.getSocket().getOutputStream().write(audio);
            client.getSocket().getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
