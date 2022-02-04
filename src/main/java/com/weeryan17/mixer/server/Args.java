package com.weeryan17.mixer.server;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(description = "config_file")
    private String configFile = "config.yml";
    @Parameter(names = {"-rt", "--receive-threads"}, description = "Maximum number of threads to create for audio receiving")
    private int maxReceiveThreads = 50;
    @Parameter(names = {"-ct", "--connect-threads"}, description = "Maximum number of threads to create for audio devices connecting. Note this isn't the number of devices that can connect, just how many the program can handle connecting at the same time")
    private int maxConnectThreads = 5;
    @Parameter(names = {"-q", "--queue-size"}, description = "Maximum length the audio queue can reach before it starts discarding audio. The bigger this number is the longer your audio can possibly be delayed, but the less audio that can potentially be discarded")
    private int maxAudioQueueSize = 5;
    @Parameter(names = {"-aub", "--audio-heartbeat"}, description = "The heartbeat interval in milliseconds for audio clients")
    private long audioHeartbeat = 15000;
    @Parameter(names = {"-apb", "--api-heartbeat"}, description = "The heartbeat interval in milliseconds for the api")
    private long apiHeartbeat = 15000;
    @Parameter(names = {"-bp", "--broadcast-port"}, description = "The port to listen for broadcasts on. It is not recommended to change this as then clients might not be able to connect")
    private int broadcastPort = 10255;
    @Parameter(names = {"-aup", "--audio-udp-port", "--udp-port"}, description = "Port to accept tcp audio traffic on. Set to 0 for a dynamic port")
    private int audioUdpPort = 0;
    @Parameter(names = {"-atp", "--audio-tcp-port"}, description = "Port to accept udp audio traffic on. Set to 0 for a dynamic port")
    private int audioTcpPort = 0;
    @Parameter(names = {"-api", "--api-port", "--api-tcp-port"}, description = "Port to accept api requests on. Set to 0 for a dynamic port")
    private int tcpPort = 0;
    @Parameter(names = {"-h", "--help"}, help = true, description = "Displays the help message")
    private boolean help;
    @Parameter(names = {"-c", "--compress"}, description = "Sets weather to compress the audio when sending to a client or not.")
    private boolean compress = true;
    @Parameter(names = {"-l", "--leeway"}, description = "Sets heartbeat leeway in milliseconds")
    private long leeway = 3000;

    public String getConfigFile() {
        return configFile;
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

    public boolean isHelp() {
        return help;
    }

    public int getBroadcastPort() {
        return broadcastPort;
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
}
