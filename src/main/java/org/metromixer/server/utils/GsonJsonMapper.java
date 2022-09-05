package org.metromixer.server.utils;

import com.google.gson.Gson;
import io.javalin.plugin.json.JsonMapper;
import io.javalin.plugin.openapi.ModelConverterFactory;
import io.javalin.plugin.openapi.jackson.ToJsonMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class GsonJsonMapper implements JsonMapper, ToJsonMapper, ModelConverterFactory, ModelConverter {

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

    @NotNull
    @Override
    public String map(@NotNull Object o) {
        return gson.toJson(o);
    }

    @NotNull
    @Override
    public ModelConverter create() {
        return this;
    }

    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        Schema schema = new Schema();
        Type javaType = type.getType();
        for (Field field : javaType.getClass().getDeclaredFields()) {
            schema.addProperties(field.getName(), new Schema());
        }
        return null;
    }
}
