package org.metromixer.server;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.metromixer.server.data.managers.SqliteManager;
import org.metromixer.server.web.WebController;
import org.metromixer.server.socket.audio.AudioConnectSocketRunnable;
import org.metromixer.server.socket.BroadcastSocketRunnable;
import org.metromixer.shared.models.Version;
import org.metromixer.rudp.ReliableServerSocket;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.jaudiolibs.jnajack.Jack;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Timer;

public class MixerServer {

    private static MixerServer INS;

    private Version serverVersion;
    private final Version audioVersion = new Version(1, 0, 0);
    private final Version apiVersion = new Version(1, 0, 0);

    private Config config;

    private Jack jack;

    public static void main(String... args) {
        try {
            INS = new MixerServer();
            INS.init(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long broadCastThreadId;
    private long audioThreadId;
    private int audioUdpPort = 1;
    private int audioTcpPort = 1;
    private int tcpPort = 1;
    private SqliteManager sqliteManager;

    public void init(String... args) throws Exception {
        Args argObj = new Args();
        JCommander jCommander = JCommander.newBuilder().addObject(argObj).build();
        jCommander.parse(args);

        if (argObj.isHelp()) {
            jCommander.usage();
            return;
        }

        config = new Config(argObj);

        sqliteManager = new SqliteManager("data.db");

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
        serverVersion = Version.fromString(properties.getProperty("server.version"));
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create();

        DatagramSocket broadcastSocket = new DatagramSocket(config.getBroadcastPort());
        broadcastSocket.setBroadcast(true);
        ReliableServerSocket audioServerSocket;
        if (config.getAudioListen() != null) {
            audioServerSocket = new ReliableServerSocket(config.getAudioUdpPort(), 0, InetAddress.getByName(config.getAudioListen()));
        } else {
            audioServerSocket = new ReliableServerSocket(config.getAudioUdpPort());
        }
        audioUdpPort = audioServerSocket.getLocalPort();

        Thread broadcastThread = new Thread(new BroadcastSocketRunnable(broadcastSocket, gson), "broadcastThread");
        broadcastThread.start();
        broadCastThreadId = broadcastThread.getId();

        Thread audioThread = new Thread(new AudioConnectSocketRunnable(audioServerSocket), "audioThread");
        audioThread.start();
        audioThreadId = audioThread.getId();

        Timer timer = new Timer();
        timer.schedule(new HeartBeatTask(), 1000, 500);

        WebController webController = new WebController(gson, config);
        webController.initController();

        tcpPort = webController.getApiPort();
        audioTcpPort = webController.getMixerPort();

        jack = Jack.getInstance();
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

    public Jack getJack() {
        return jack;
    }

    public Config getConfig() {
        return config;
    }

    public SqliteManager getSqliteManager() {
        return sqliteManager;
    }

    public static MixerServer getInstance() {
        if (INS == null) {
            throw new RuntimeException("Tried to get instance of main class without running main class");
        }
        return INS;
    }

    public Version getServerVersion() {
        return serverVersion;
    }

    public Version getAudioVersion() {
        return audioVersion;
    }

    public Version getApiVersion() {
        return apiVersion;
    }
}
