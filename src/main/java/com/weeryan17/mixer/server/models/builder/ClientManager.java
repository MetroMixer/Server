package com.weeryan17.mixer.server.models.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.PendingContainer;
import com.weeryan17.mixer.shared.command.data.IdentifyProperties;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    private List<Client> clientList = new ArrayList<>();
    private List<PendingContainer> pendingClients = new ArrayList<>();

    private Gson gson;
    private long heartbeat;
    public ClientManager(Gson gson, long heartbeat) {
        this.gson = gson;
        this.heartbeat = heartbeat;
    }

    public Client buildClient(String key, IdentifyProperties id) {
        Client client = new Client(gson, id, heartbeat);
        client.setKey(key);
        clientList.add(client);
        pendingClients.remove(pendingClients.stream().filter(pendingContainer -> pendingContainer.getIdentifyProperties().equals(id)).findFirst().orElse(null));
        return client;
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

    public List<PendingContainer> getPendingClients() {
        return pendingClients;
    }

    private static ClientManager INS;
    public static ClientManager getInstance() {
        if (INS == null) {
            INS = new ClientManager(new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create(), MixerServer.getInstance().getConfig().getAudioHeartbeat());
        }
        return INS;
    }

}
