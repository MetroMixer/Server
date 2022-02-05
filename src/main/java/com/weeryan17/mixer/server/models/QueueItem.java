package com.weeryan17.mixer.server.models;

import java.nio.FloatBuffer;
import java.util.List;

public class QueueItem {

    private List<FloatBuffer> floatBuffers;
    private long startTime;

    public QueueItem(List<FloatBuffer> floatBuffers, long startTime) {
        this.floatBuffers = floatBuffers;
        this.startTime = startTime;
    }

    public List<FloatBuffer> getFloatBuffers() {
        return floatBuffers;
    }

    public void setFloatBuffers(List<FloatBuffer> floatBuffers) {
        this.floatBuffers = floatBuffers;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
