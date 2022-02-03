package com.weeryan17.mixer.server.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.weeryan17.mixer.server.MixerServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class BroadcastSocketRunnable implements Runnable {

    private DatagramSocket broadCastSocket;
    private int bufSize;
    private Gson gson;

    public BroadcastSocketRunnable(DatagramSocket broadCastSocket, Gson gson) throws SocketException {
        this.broadCastSocket = broadCastSocket;
        bufSize = broadCastSocket.getReceiveBufferSize();
        this.gson = gson;
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
            String jsonStr = new String(data, StandardCharsets.UTF_8).trim();
            JsonObject object;
            try {
                object = JsonParser.parseString(jsonStr).getAsJsonObject();
            } catch (JsonParseException e) {
                e.printStackTrace();
                //TODO log
                continue;
            }
            if (!object.has("mixer")) {
                //TODO log
                continue;
            }
            int port = object.get("port").getAsInt();

            //TODO handle already approved clients trying to connect.
            try {
                Socket socket = new Socket(ip, port);
                String thisAddress = socket.getLocalAddress().getHostAddress();
                JsonObject toSend = new JsonObject();
                toSend.addProperty("ip", thisAddress);
                toSend.addProperty("audio_port", MixerServer.getInstance().getAudioTcpPort());
                toSend.addProperty("api_port", MixerServer.getInstance().getTcpPort());
                socket.getOutputStream().write(gson.toJson(toSend).getBytes(StandardCharsets.UTF_8));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
