package com.weeryan17.mixer.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weeryan17.mixer.server.rest.WebController;
import com.weeryan17.mixer.server.socket.audio.AudioConnectSocketRunnable;
import com.weeryan17.mixer.server.socket.BroadcastSocketRunnable;
import com.weeryan17.mixer.shared.models.Version;
import com.weeryan17.rudp.ReliableServerSocket;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.jaudiolibs.jnajack.Jack;

import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.util.Properties;
import java.util.Timer;

public class MixerServer {

    private static MixerServer INS;

    private Version serverVersion;
    private final Version audioVersion = new Version(1, 0, 0);
    private final Version apiVersion = new Version(1, 0, 0);

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
    private int udpPort;
    private int tcpPort;

    public void init(String... args) throws Exception {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
        serverVersion = Version.fromString(properties.getProperty("server.version"));
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create();

        DatagramSocket broadcastSocket = new DatagramSocket(10255);
        broadcastSocket.setBroadcast(true);
        ReliableServerSocket audioServerSocket = new ReliableServerSocket(0);
        udpPort = audioServerSocket.getLocalPort();

        Thread broadcastThread = new Thread(new BroadcastSocketRunnable(broadcastSocket, gson), "broadcastThread");
        broadcastThread.start();
        broadCastThreadId = broadcastThread.getId();

        Thread audioThread = new Thread(new AudioConnectSocketRunnable(audioServerSocket), "audioThread");
        audioThread.start();
        audioThreadId = audioThread.getId();

        Timer timer = new Timer();
        timer.schedule(new HeartBeatTask(), 1000, 500);

        WebController webController = new WebController(gson);
        tcpPort = webController.initController();

        Jack jack = Jack.getInstance();

        /*JackClient client = jack.openClient("java-test", EnumSet.noneOf(JackOptions.class), EnumSet.noneOf(JackStatus.class));
        JackPort in1 = client.registerPort("test-1", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsInput));
        JackPort in2 = client.registerPort("test-2", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsInput));
        JackPort out1 = client.registerPort("test-1", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsOutput));
        JackPort out2 = client.registerPort("test-2", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsOutput));
        client.setProcessCallback(new JackProcessCallback() {
            @Override
            public boolean process(JackClient client, int nframes) {
                FloatBuffer floatBuffer = FloatBuffer.wrap()
                out1.getFloatBuffer().put(in1.getFloatBuffer());
                return false;
            }
        });*/
    }

    public int getUdpPort() {
        return udpPort;
    }

    public int getTcpPort() {
        return tcpPort;
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
