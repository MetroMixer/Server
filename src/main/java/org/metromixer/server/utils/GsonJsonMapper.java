package org.metromixer.server.utils;

import com.google.gson.Gson;
import io.javalin.plugin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GsonJsonMapper implements JsonMapper {

    private Gson gson;
    public GsonJsonMapper(Gson gson) {
        this.gson = gson;
    }

    @NotNull
    @Override
    public String toJsonString(@NotNull Object obj) {
        return gson.toJson(obj);
    }

    @NotNull
    @Override
    public InputStream toJsonStream(@NotNull Object obj) {
        return new ByteArrayInputStream(gson.toJson(obj).getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    @Override
    public <T> T fromJsonString(@NotNull String json, @NotNull Class<T> targetClass) {
        return gson.fromJson(json, targetClass);
    }

    @NotNull
    @Override
    public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Class<T> targetClass) {
        return gson.fromJson(new InputStreamReader(json), targetClass);
    }
}
