package org.metromixer.server.models.exceptions;

import io.javalin.http.HttpResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AuthException extends HttpResponseException {
    public AuthException(@NotNull String message) {
        super(401, message);
    }

}
