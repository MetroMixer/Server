package org.metromixer.server.web;

import com.google.gson.Gson;
import org.metromixer.server.Config;
import org.metromixer.server.models.web.WebHandlerObjects;
import org.metromixer.server.utils.GsonJsonMapper;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebController {

    private Gson gson;
    private Config config;

    private int apiPort;
    private int mixerPort;

    public WebController(Gson gson, Config config) {
        this.gson = gson;
        this.config = config;
    }

    public void initController() {
        WebHandlerObjects handlerObjects = new WebHandlerObjects(gson);
        apiPort = initApiController(handlerObjects);
        mixerPort = initMixerController(handlerObjects);
    }

    private int initApiController(WebHandlerObjects handlerObjects) {
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.jsonMapper(new GsonJsonMapper(gson));
            javalinConfig.showJavalinBanner = false;
            javalinConfig.enableCorsForAllOrigins();
        });

        Map<String, List<WebHandler>> webHandlerMap = getWebHandlers("org.metromixer.server.web.routes.api", handlerObjects);

        loadWebHandlers(app, webHandlerMap);

        int port = config.getTcpPort();
        String listen = "0.0.0.0";

        if (config.getApiListen() != null) {
            listen = config.getApiListen();
        }

        app.start(listen, port);
        return app.port();
    }

    private int initMixerController(WebHandlerObjects handlerObjects) {
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.jsonMapper(new GsonJsonMapper(gson));
            javalinConfig.showJavalinBanner = false;
            javalinConfig.enableCorsForAllOrigins();
        });

        Map<String, List<WebHandler>> webHandlerMap = getWebHandlers("org.metromixer.mixer.server.web.routes.mixer", handlerObjects);

        loadWebHandlers(app, webHandlerMap);

        int port = config.getAudioTcpPort();
        String listen = "0.0.0.0";

        if (config.getAudioListen() != null) {
            listen = config.getApiListen();
        }

        app.start(listen, port);
        return app.port();
    }

    private Map<String, List<WebHandler>> getWebHandlers(String packagePath, WebHandlerObjects handlerObjects) {
        Map<String, List<WebHandler>> webHandlerMap = new HashMap<>();

        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> webHandlers = reflections.getTypesAnnotatedWith(WebRoot.class);
        for (Class<?> clazz : webHandlers) {
            WebHandler webHandler;
            try {
                webHandler = (WebHandler) clazz.getConstructor(WebHandlerObjects.class).newInstance(handlerObjects);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                // TODO log
                continue;
            }

            WebRoot root = clazz.getAnnotation(WebRoot.class);
            String url = root.url();
            List<WebHandler> toModify;
            if (webHandlerMap.containsKey(url)) {
                toModify = webHandlerMap.get(url);
            } else {
                toModify = new ArrayList<>();
                webHandlerMap.put(url, toModify);
            }
            toModify.add(webHandler);
        }

        return webHandlerMap;
    }

    private void loadWebHandlers(Javalin app, Map<String, List<WebHandler>> routes) {
        for (Map.Entry<String, List<WebHandler>> entry : routes.entrySet()) {
            app.routes(() -> {
                ApiBuilder.path(entry.getKey(), () -> {
                    for (WebHandler handler : entry.getValue()) {
                        handler.init();
                    }
                });
            });
        }
    }

    public int getApiPort() {
        return apiPort;
    }

    public int getMixerPort() {
        return mixerPort;
    }
}
