package org.metromixer.server.models.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.metromixer.server.MixerServer;
import org.metromixer.server.models.ApiSession;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;

public class ApiManager {

    private Gson gson;
    private long heartbeat;
    public ApiManager(Gson gson, long heartbeat) {
        this.gson = gson;
        this.heartbeat = heartbeat;
    }

    private List<ApiSession> sessions = new ArrayList<>();

    public ApiSession createSession(String key) {
        ApiSession apiSession = new ApiSession(key);
        sessions.add(apiSession);
        return apiSession;
    }

    public List<ApiSession> getSessions() {
        return sessions;
    }

    public ApiSession getSessionFromKey(String key) {
        return sessions.stream().filter(apiSession -> apiSession.getKey().equals(key)).findFirst().orElse(null);
    }

    public ApiSession getSessionFromWebsocket(Session session) {
        return sessions.stream().filter(apiSession -> apiSession.getSession().equals(session)).findFirst().orElse(null);
    }

    public long getHeartbeat() {
        return heartbeat;
    }

    private static ApiManager INS;
    public static ApiManager getInstance() {
        if (INS == null) {
            INS = new ApiManager(new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create(), MixerServer.getInstance().getConfig().getApiHeartbeat());
        }
        return INS;
    }

}
