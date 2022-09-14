package org.metromixer.server.socket.audio;

import org.metromixer.server.MixerServer;
import org.metromixer.server.models.Client;
import org.metromixer.server.models.QueueItem;
import org.metromixer.server.models.managers.ClientManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public class AudioProcessRunnable implements Runnable {

    private final byte[] audio;
    private final Client client;
    private final long start;
    public AudioProcessRunnable(byte[] audio, String key, long start) {
        Client client = ClientManager.getInstance().getClientWithKey(key);
        if (client == null) {
            throw new UnsupportedOperationException("Key not valid");
        }
        this.client = client;
        this.audio = audio;
        this.start = start;
    }

    @Override
    public void run() {
        byte[] audio = this.audio;
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(this.audio));
            audio = gzipInputStream.readAllBytes();
        } catch (ZipException ignored) {

        } catch (IOException e) {
            MixerServer.getInstance().getLogger().warn("Error de-compressing audio", e);
            return;
        }
        //System.out.println(bytesToHex(audio));
        ByteBuffer buffer = ByteBuffer.wrap(audio);
        int channels = buffer.getInt();
        List<FloatBuffer> floatBuffers = new ArrayList<>();
        for (int channel = 0; channel < channels; channel++) {
            int floats = buffer.getInt();
            FloatBuffer floatBuffer = FloatBuffer.allocate(floats);
            for (int i = 0; i < floats; i++) {
                floatBuffer.put(buffer.getFloat());
            }
            floatBuffer.rewind();
            floatBuffers.add(floatBuffer);
        }

        while (floatBuffers.get(0).hasRemaining()) {
            List<Float> floats = new ArrayList<>();
            for (int i = 0; i < floatBuffers.size(); i++) {
                FloatBuffer floatBuffer = floatBuffers.get(i);
                floats.add(i, floatBuffer.get());
            }
            client.addToQueue(new QueueItem(floats, start));
        }

    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
