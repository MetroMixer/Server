package com.weeryan17.mixer.server.models;

import com.google.common.collect.EvictingQueue;
import com.google.gson.Gson;
import com.weeryan17.mixer.server.MixerServer;
import com.weeryan17.mixer.server.socket.audio.AudioSendRunnable;
import com.weeryan17.mixer.server.utils.ThreadExecutorContainer;
import com.weeryan17.mixer.shared.command.data.IdentifyProperties;
import com.weeryan17.mixer.shared.models.ChannelType;
import com.weeryan17.rudp.ReliableServerSocket;
import org.eclipse.jetty.websocket.api.Session;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPort;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackProcessCallback;
import org.jaudiolibs.jnajack.JackStatus;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Client {

    private transient Gson gson;

    private transient JackClient jackClient;

    private transient List<JackPort> inputs = new ArrayList<>();
    private transient List<JackPort> outputs = new ArrayList<>();

    private transient Queue<List<FloatBuffer>> floatBuffersQueue = EvictingQueue.create(MixerServer.getInstance().getConfig().getMaxAudioQueueSize());

    private transient Socket socket;

    private IdentifyProperties id;

    private long beatInterval;

    private transient ThreadExecutorContainer sendContainer;

    public Client(Gson gson, IdentifyProperties id, long beatInterval, ThreadExecutorContainer sendContainer) throws JackException {
        this.gson = gson;
        this.beatInterval = beatInterval;
        this.id = id;
        this.sendContainer = sendContainer;
        jackClient = MixerServer.getInstance().getJack().openClient(id.getId(), EnumSet.noneOf(JackOptions.class), EnumSet.noneOf(JackStatus.class));
        jackClient.setProcessCallback(new Processor(this));
    }

    public int createChannel(String name, ChannelType type) throws JackException {
        //We reverse here as an "input" will send output to the rest of the jack network, and vise versa.
        JackPort port = jackClient.registerPort(name, JackPortType.AUDIO, EnumSet.of(type.equals(ChannelType.IN) ? JackPortFlags.JackPortIsOutput : JackPortFlags.JackPortIsInput));
        if (type.equals(ChannelType.IN)) {
            inputs.add(port);
            return inputs.indexOf(port);
        } else {
            outputs.add(port);
            return outputs.indexOf(port);
        }
    }

    public long getBeatInterval() {
        return beatInterval;
    }

    private Session session;
    private String key;
    private long lastBeatTime = -1;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }

    public void updateLastBeatTime() {
        lastBeatTime = System.currentTimeMillis();
    }

    public void shutdown() {
        session = null;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    private class Processor implements JackProcessCallback {

        private Client client;
        private Processor(Client client) {
            this.client = client;
        }

        @Override
        public boolean process(JackClient client, int nframes) {
            List<FloatBuffer> floatBuffers = floatBuffersQueue.poll();
            if (floatBuffers != null) {
                for (int i = 0; i < inputs.size(); i++) {
                    inputs.get(i).getFloatBuffer().put(floatBuffers.get(i));
                }
            }
            List<ByteBuffer> toSend = new ArrayList<>();
            int size = 0;
            for (JackPort port : outputs) {
                FloatBuffer fBuffer = port.getFloatBuffer();
                ByteBuffer buffer = ByteBuffer.allocate((fBuffer.array().length * 4) + 4);
                buffer.putInt(fBuffer.array().length);
                for (float f : fBuffer.array()) {
                    buffer.putFloat(f);
                }
                toSend.add(buffer);
                size += buffer.array().length;
            }
            if (toSend.size() > 0) {
                ByteBuffer send = ByteBuffer.allocate(size + 4);
                send.putInt(toSend.size());
                for (ByteBuffer buffer : toSend) {
                    send.put(buffer);
                }
                sendContainer.queueThread(new AudioSendRunnable(this.client, send.array()));
            }

            return true;
        }
    }

}
