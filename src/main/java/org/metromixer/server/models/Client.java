package org.metromixer.server.models;

import com.google.gson.Gson;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.metromixer.server.MixerServer;
import org.metromixer.server.socket.audio.AudioSendRunnable;
import org.metromixer.server.utils.ThreadExecutorContainer;
import org.metromixer.shared.command.data.IdentifyProperties;
import org.metromixer.shared.models.ChannelType;
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
import java.util.List;
import java.util.Queue;

public class Client {

    private transient JackClient jackClient;

    private transient List<JackPort> inputs = new ArrayList<>();
    private transient List<JackPort> outputs = new ArrayList<>();

    //private transient Queue<Float[]> pastInputs;

    private transient Queue<QueueItem> floatBuffersQueue;

    private transient Socket socket;

    private IdentifyProperties id;

    private long beatInterval;

    private transient ThreadExecutorContainer sendContainer;

    public Client(IdentifyProperties id, long beatInterval, ThreadExecutorContainer sendContainer) throws JackException {
        this.beatInterval = beatInterval;
        this.id = id;
        this.sendContainer = sendContainer;
        jackClient = MixerServer.getInstance().getJack().openClient(id.getId(), EnumSet.noneOf(JackOptions.class), EnumSet.noneOf(JackStatus.class));
        //pastInputs = new CircularFifoQueue<>(jackClient.getBufferSize());
        floatBuffersQueue = new CircularFifoQueue<>(jackClient.getBufferSize() + 50);
        jackClient.setProcessCallback(new Processor(this));
        jackClient.activate();
    }

    public void addToQueue(QueueItem queueItem) {
        floatBuffersQueue.add(queueItem);
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

    private transient Session session;
    private transient String key;
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
        jackClient.deactivate();
        jackClient.close();
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
            if (floatBuffersQueue.size() >= nframes) {
                List<FloatBuffer> floatBuffers = new ArrayList<>();
                for (int i = 0; i < inputs.size(); i++) {
                    floatBuffers.add(FloatBuffer.allocate(nframes));
                }
                while (floatBuffers.get(0).hasRemaining()) {
                    QueueItem item = floatBuffersQueue.poll();
                    if (item != null) {
                        /*Float[] floats = new Float[floatBuffers.size()];
                        item.getFloats().toArray(floats);
                        pastInputs.add(floats);*/
                        for (int i = 0; i < floatBuffers.size(); i++) {
                            floatBuffers.get(i).put(item.getFloats().get(i));
                        }
                    }
                }
                for (int i = 0; i < inputs.size(); i++) {
                    floatBuffers.get(i).rewind();
                    inputs.get(i).getFloatBuffer().put(floatBuffers.get(i));
                }
            }
            List<ByteBuffer> toSend = new ArrayList<>();
            int size = 0;
            for (JackPort port : outputs) {
                FloatBuffer fBuffer = port.getFloatBuffer();
                if (fBuffer == null) {
                    continue;
                }
                ByteBuffer buffer = ByteBuffer.allocate((fBuffer.remaining() * 4) + 4);
                buffer.putInt(fBuffer.remaining());
                while (fBuffer.hasRemaining()) {
                    float f = fBuffer.get();
                    buffer.putFloat(f);
                }
                buffer.rewind();
                toSend.add(buffer);
                size += buffer.array().length;
            }
            if (toSend.size() > 0) {
                ByteBuffer send = ByteBuffer.allocate(size + 4);
                send.putInt(toSend.size());
                for (ByteBuffer buffer : toSend) {
                    send.put(buffer);
                }
                send.rewind();
                byte[] audio = new byte[send.remaining()];
                send.get(audio);
                sendContainer.queueThread(new AudioSendRunnable(this.client, audio));
            }

            return true;
        }
    }

}
