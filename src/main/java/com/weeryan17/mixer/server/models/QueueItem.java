package com.weeryan17.mixer.server.models;

import java.nio.FloatBuffer;
import java.util.List;

public class QueueItem {

    private List<Float> floats;
    private long startTime;

    public QueueItem(List<Float> floats, long startTime) {
        this.floats = floats;
        this.startTime = startTime;
    }

    public List<Float> getFloats() {
        return floats;
    }

    public void setFloats(List<Float> floats) {
        this.floats = floats;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
