package com.weeryan17.mixer.server.socket.audio;

import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.managers.ClientManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class AudioProcessRunnable implements Runnable {

    private final byte[] audio;
    private final Client client;
    public AudioProcessRunnable(byte[] audio, String key) {
        Client client = ClientManager.getInstance().getClientWithKey(key);
        if (client == null) {
            throw new UnsupportedOperationException("Key not valid");
        }
        this.client = client;
        this.audio = audio;
    }

    @Override
    public void run() {
        byte[] audio = this.audio;
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(this.audio));
            audio = gzipInputStream.readAllBytes();
        } catch (IOException ignored) {}
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
        client.addToQueue(floatBuffers);
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
