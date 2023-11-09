package com.raf.server;

import com.raf.util.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class DIEngine {
    private Map<String, Object> controllerMap;
    private Map<String, Object> routes;
    private Map<String, Object> singletonMap;

    public DIEngine() {
        this.controllerMap = new HashMap<>();
        this.routes = new HashMap<>();
        this.singletonMap = new HashMap<>();
    }

    public void putController(String key, Object value) {
        this.controllerMap.put(key, value);
    }

    public void putRoute(HttpMethod httpMethod, String path, Object value) {
        this.routes.put(hashRoute(httpMethod.toString(), path), value);
    }

    public boolean isSingletonCreated(String key) {
        return this.singletonMap.containsKey(key);
    }

    public Object getSingleton(String key) {
        return this.singletonMap.get(key);
    }

    public void addSingleton(String key, Object value) {
        this.singletonMap.put(key, value);
    }

    public Object findRoute(String httpMethod, String path) {
        return this.routes.get(hashRoute(httpMethod, path));
    }

    private String hashRoute(String httpMethod, String path) {
        return httpMethod + " " + path;
    }
}
