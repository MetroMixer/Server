package org.metromixer.server.models.web.api.device;

import org.metromixer.shared.command.data.IdentifyProperties;

public class SimpleClient {

    private IdentifyProperties id;
    private long beatInterval;
    private long lastBeatTime = -1;

    public SimpleClient(IdentifyProperties id, long beatInterval, long lastBeatTime) {
        this.id = id;
        this.beatInterval = beatInterval;
        this.lastBeatTime = lastBeatTime;
    }

    public IdentifyProperties getId() {
        return id;
    }

    public void setId(IdentifyProperties id) {
        this.id = id;
    }

    public long getBeatInterval() {
        return beatInterval;
    }

    public void setBeatInterval(long beatInterval) {
        this.beatInterval = beatInterval;
    }

    public long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }
}
