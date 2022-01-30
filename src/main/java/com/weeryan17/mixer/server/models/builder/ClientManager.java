package com.weeryan17.mixer.server.models.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weeryan17.mixer.server.models.Client;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    private List<Client> clientList = new ArrayList<>();

    private Gson gson;
    private long heartbeat = 15000;
    public ClientManager(Gson gson) {
        this.gson = gson;
    }

    public void buildClient(String ip, int port) throws IOException {
        Client client = new Client(gson, heartbeat);
        client.connect(ip, port);
        clientList.add(client);
    }

    public Client getClientWithKey(String key) {
        return clientList.stream().filter(client -> client.getKey().equals(key)).findFirst().orElse(null);
    }

    public Client getClientWithSession(Session session) {
        return clientList.stream().filter(client -> client.getSession().equals(session)).findFirst().orElse(null);
    }

    public long getHeartbeat() {
        return heartbeat;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    private static ClientManager INS;
    public static ClientManager getInstance() {
        if (INS == null) {
            INS = new ClientManager(new GsonBuilder().create());
        }
        return INS;
    }

}
