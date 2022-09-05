package com.weeryan17.mixer.server.models.exceptions;

public class DeviceDoesNotExistException extends Exception {

    private final String deviceId;

    public DeviceDoesNotExistException(String deviceId) {
        super();
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
