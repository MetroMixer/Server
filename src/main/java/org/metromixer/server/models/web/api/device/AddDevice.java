package org.metromixer.server.models.web.api.device;

public class AddDevice {

    private String endpoint;
    private int port;

    public AddDevice(String endpoint, int port) {
        this.endpoint = endpoint;
        this.port = port;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
