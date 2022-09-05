package com.weeryan17.mixer.server.models.web.api.device;

public class ApproveDevice {

    private String deviceId;

    public ApproveDevice(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
