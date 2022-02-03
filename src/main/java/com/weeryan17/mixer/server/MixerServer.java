package com.weeryan17.mixer.server;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weeryan17.mixer.server.data.managers.SqliteManager;
import com.weeryan17.mixer.server.rest.MixerWebController;
import com.weeryan17.mixer.server.rest.WebController;
import com.weeryan17.mixer.server.socket.audio.AudioConnectSocketRunnable;
import com.weeryan17.mixer.server.socket.BroadcastSocketRunnable;
import com.weeryan17.mixer.shared.models.Version;
import com.weeryan17.rudp.ReliableServerSocket;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPort;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackProcessCallback;
import org.jaudiolibs.jnajack.JackStatus;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.FloatBuffer;
import java.util.EnumSet;
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
    private int audioUdpPort;
    private int audioTcpPort;
    private int tcpPort;
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

        MixerWebController mixerWebController = new MixerWebController(gson);
        audioTcpPort = mixerWebController.initController();

        WebController webController = new WebController(gson);
        tcpPort = webController.initController();

        jack = Jack.getInstance();

        /*JackClient client = jack.openClient("java-test", EnumSet.noneOf(JackOptions.class), EnumSet.noneOf(JackStatus.class));
        JackPort in1 = client.registerPort("test-1", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsInput));
        JackPort in2 = client.registerPort("test-2", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsInput));
        JackPort out1 = client.registerPort("test-1", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsOutput));
        JackPort out2 = client.registerPort("test-2", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsOutput));
        client.setProcessCallback(new JackProcessCallback() {
            @Override
            public boolean process(JackClient client, int nframes) {
                out1.getFloatBuffer().put(in1.getFloatBuffer());
                out2.getFloatBuffer().put(in2.getFloatBuffer());
                return true;
            }
        });*/
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
