package org.metromixer.server;

import org.metromixer.server.models.ApiSession;
import org.metromixer.server.models.Client;
import org.metromixer.server.models.managers.ApiManager;
import org.metromixer.server.models.managers.ClientManager;

import java.util.List;
import java.util.TimerTask;

public class HeartBeatTask extends TimerTask {

    private long leeway;

    public HeartBeatTask() {
        leeway = MixerServer.getInstance().getConfig().getLeeway();
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        List<Client> clients = ClientManager.getInstance().getClientList();
        for (Client client : clients) {
            if (client.getLastBeatTime() != -1) {
                long check = client.getLastBeatTime() + client.getBeatInterval() + leeway;
                if (time > check) {
                    client.shutdown();
                    ClientManager.getInstance().getClientList().remove(client);
                    //TODO log, and attempt to send shutdown message
                }
            }
        }
        List<ApiSession> apiSessions = ApiManager.getInstance().getSessions();
        for (ApiSession apiSession : apiSessions) {
            if (apiSession.getLastBeatTime() != -1) {
                long check = apiSession.getLastBeatTime() + ApiManager.getInstance().getHeartbeat() + leeway;
                if (time > check) {
                    apiSession.disconnect();
                }
            }
        }
        //TODO basically the same code but for api
    }

}
