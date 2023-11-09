package com.raf.server;

import com.raf.util.Pair;

import java.util.HashMap;
import java.util.List;

public class DependencyContainer {
    private HashMap<Pair<Class<?>, String>, Class<?>> map = new HashMap<>();

    public void addNewImplementation(Class<?> intface, String name, Class<?> implementation) {
        if(!intface.isInterface())
            throw new RuntimeException("Class " + intface.getName() + " is not an interface!");
        if(implementation.isInterface())
            throw new RuntimeException("Class " + implementation.getName() + " is an interface!");
        if(!intface.isAssignableFrom(implementation))
            throw new RuntimeException("Class " + implementation.getName() + " does not implement interface " + intface.getName() + "!");

        Pair<Class<?>, String> key = new Pair<>(intface, name);
        if(map.containsKey(key))
            throw new RuntimeException("Class " + implementation.getName() + " already registered for interface " + intface.getName() + "!");

        map.put(key, implementation);
    }

    public Class<?> getImplementation(Class<?> intface, String name) {
        Pair<Class<?>, String> key = new Pair<>(intface, name);
        if(!map.containsKey(key))
            throw new RuntimeException("No implementation registered for interface " + intface.getName() + "!");
        return map.get(key);
    }

}
