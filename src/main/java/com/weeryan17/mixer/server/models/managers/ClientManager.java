package com.weeryan17.mixer.server.models.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.data.entities.ApprovedClient;
import com.weeryan17.mixer.server.models.Client;
import com.weeryan17.mixer.server.models.PendingContainer;
import com.weeryan17.mixer.server.utils.ThreadExecutorContainer;
import com.weeryan17.mixer.shared.command.data.IdentifyProperties;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.jaudiolibs.jnajack.JackException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientManager {

    private List<Client> clientList = new CopyOnWriteArrayList<>();
    private List<PendingContainer> pendingClients = new CopyOnWriteArrayList<>();

    private final ThreadExecutorContainer sendContainer;

    private Gson gson;
    private long heartbeat;
    public ClientManager(Gson gson, long heartbeat) {
        sendContainer = new ThreadExecutorContainer("AudioSendProcess", 50);
        this.gson = gson;
        this.heartbeat = heartbeat;
    }

    public Client buildClient(String key, IdentifyProperties id) throws JackException {
        Client client = new Client(gson, id, heartbeat, sendContainer);
        client.setKey(key);
        clientList.add(client);
        pendingClients.remove(pendingClients.stream().filter(pendingContainer -> pendingContainer.getIdentifyProperties().equals(id)).findFirst().orElse(null));
        MixerServer.getInstance().getSqliteManager().transaction(session -> {
            ApprovedClient approvedClient = new ApprovedClient(key);
            session.persist(approvedClient);
        });
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
