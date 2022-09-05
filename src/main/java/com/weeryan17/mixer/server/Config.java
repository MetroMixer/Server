package com.weeryan17.mixer.server;

import com.beust.jcommander.Parameter;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private Args commandLineArgs;
    public Config(Args commandLineArgs) throws IOException {
        this.commandLineArgs = commandLineArgs;
        init();
    }

    private int maxReceiveThreads;
    private int maxConnectThreads;
    private int maxAudioQueueSize;
    private long audioHeartbeat;
    private long apiHeartbeat;
    private int broadcastPort;
    private String audioListen;
    private String apiListen;
    private int audioUdpPort;
    private int audioTcpPort;
    private int tcpPort;
    private boolean compress;
    private long leeway;

    private String apiPassword;

    public void init() throws IOException {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new File(commandLineArgs.getConfigFile()));

        maxReceiveThreads = fileConfiguration.getInt("threads.receive", commandLineArgs.getMaxReceiveThreads());
        maxConnectThreads = fileConfiguration.getInt("threads.connect", commandLineArgs.getMaxConnectThreads());
        maxAudioQueueSize = fileConfiguration.getInt("audio.queue", commandLineArgs.getMaxAudioQueueSize());
        audioHeartbeat = fileConfiguration.getLong("heartbeat.audio", commandLineArgs.getAudioHeartbeat());
        apiHeartbeat = fileConfiguration.getLong("heartbeat.api", commandLineArgs.getApiHeartbeat());
        leeway = fileConfiguration.getLong("heartbeat.leeway", commandLineArgs.getLeeway());
        broadcastPort = fileConfiguration.getInt("net.broadcast", commandLineArgs.getBroadcastPort());
        audioListen = fileConfiguration.getString("net.audio.listen", null);
        audioUdpPort = fileConfiguration.getInt("net.audio.udp", commandLineArgs.getAudioUdpPort());
        audioTcpPort = fileConfiguration.getInt("net.audio.tcp", commandLineArgs.getAudioTcpPort());
        apiListen = fileConfiguration.getString("net.api.listen", null);
        tcpPort = fileConfiguration.getInt("net.api.port", commandLineArgs.getTcpPort());
        compress = fileConfiguration.getBoolean("audio.compress", commandLineArgs.shouldCompress());
        apiPassword = fileConfiguration.getString("net.api.password");

    }

    public int getMaxReceiveThreads() {
        return maxReceiveThreads;
    }

    public int getMaxConnectThreads() {
        return maxConnectThreads;
    }

    public int getMaxAudioQueueSize() {
        return maxAudioQueueSize;
    }

    public long getAudioHeartbeat() {
        return audioHeartbeat;
    }

    public long getApiHeartbeat() {
        return apiHeartbeat;
    }

    public int getBroadcastPort() {
        return broadcastPort;
    }

    public String getAudioListen() {
        return audioListen;
    }

    public String getApiListen() {
        return apiListen;
    }

    public int getAudioUdpPort() {
        return audioUdpPort;
    }

    public int getAudioTcpPort() {
        return audioTcpPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public boolean shouldCompress() {
        return compress;
    }

    public long getLeeway() {
        return leeway;
    }

    public String getApiPassword() {
        return apiPassword;
    }
}
