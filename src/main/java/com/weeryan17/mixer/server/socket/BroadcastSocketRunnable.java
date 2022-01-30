package com.weeryan17.mixer.server.socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.weeryan17.mixer.server.models.builder.ClientManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class BroadcastSocketRunnable implements Runnable {

    DatagramSocket broadCastSocket;
    int bufSize;

    public BroadcastSocketRunnable(DatagramSocket broadCastSocket) throws SocketException {
        this.broadCastSocket = broadCastSocket;
        bufSize = broadCastSocket.getReceiveBufferSize();
    }

    @Override
    public void run() {
        while (true) {
            byte[] buffer = new byte[bufSize];
            DatagramPacket receive = new DatagramPacket(buffer, bufSize);
            try {
                broadCastSocket.receive(receive);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            byte[] data = receive.getData();
            String ip = receive.getAddress().getHostAddress();
            JsonObject object;
            try {
                object = JsonParser.parseString(new String(data, StandardCharsets.UTF_8)).getAsJsonObject();
            } catch (JsonParseException e) {
                //TODO log
                continue;
            }
            if (!object.has("mixer")) {
                //TODO log
                continue;
            }
            int port = object.get("port").getAsInt();

            try {
                ClientManager.getInstance().buildClient(ip, port);
            } catch (IOException e) {
                //TODO log
                e.printStackTrace();
            }
        }
    }

}
