package com.raf.server;

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

    public void putRoute(String key, Object value) {
        this.routes.put(key, value);
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
}
