package org.metromixer.server.models.exceptions;

import io.javalin.http.HttpResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DeviceDoesNotExistException extends HttpResponseException {


    public DeviceDoesNotExistException(@NotNull String message, String deviceId) {
        super(404, message, Map.of("device_id", deviceId));
    }

}
