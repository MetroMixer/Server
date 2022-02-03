package com.weeryan17.mixer.server.models;

import com.weeryan17.mixer.shared.command.data.IdentifyProperties;

import java.util.function.Consumer;
import java.util.function.Function;

public class PendingContainer {

    private IdentifyProperties identifyProperties;
    private Function<Boolean, Throwable> acceptConsumer;

    public PendingContainer(IdentifyProperties identifyProperties, Function<Boolean, Throwable> acceptConsumer) {
        this.identifyProperties = identifyProperties;
        this.acceptConsumer = acceptConsumer;
    }

    public IdentifyProperties getIdentifyProperties() {
        return identifyProperties;
    }

    public void setIdentifyProperties(IdentifyProperties identifyProperties) {
        this.identifyProperties = identifyProperties;
    }

    public Function<Boolean, Throwable> getAcceptConsumer() {
        return acceptConsumer;
    }

    public void setAcceptConsumer(Function<Boolean, Throwable> acceptConsumer) {
        this.acceptConsumer = acceptConsumer;
    }
}
