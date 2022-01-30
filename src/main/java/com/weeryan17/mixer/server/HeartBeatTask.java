package com.weeryan17.mixer.server;

import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.builder.ClientManager;

import java.util.List;
import java.util.TimerTask;

public class HeartBeatTask extends TimerTask {

    private long leway = 1000;

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        List<Client> clients = ClientManager.getInstance().getClientList();
        for (Client client : clients) {
            long check = client.getLastBeatTime() + client.getBeatInterval() + leway;
            if (time > check) {
                client.shutdown();
            }
        }
    }

}
