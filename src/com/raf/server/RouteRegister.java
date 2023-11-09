package com.raf.server;


import com.raf.annotations.Controller;
import com.raf.annotations.Path;
import com.raf.controller.ControllerTest;
import com.raf.controller.ControllerTest2;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.reflections.scanners.Scanners.*;

public class RouteRegister {
    public static Map<String, Object> controllerMap;
    public static Map<String, Object> routes;
    public static Map<String, Object> singletonMap;

    public RouteRegister() {
        controllerMap = new HashMap<>();
        routes = new HashMap<>();
        singletonMap = new HashMap<>();
    }

    public void registerRoutes() {
        System.out.println("Registering routes...");
    }


    public void registerControllers() {
        System.out.println("Registering controllers...");

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage("com.raf.controller")
                        .filterInputsBy(new FilterBuilder().includePackage("com.raf.controller"))
                        .setScanners(TypesAnnotated, MethodsAnnotated, MethodsReturn));

        Set<Class<?>> controllers = reflections.get(TypesAnnotated.with(Controller.class).asClass());
        System.out.println("Number of registered controllers: " + controllers.size());
        for (Class<?> aClass : controllers) {
            System.out.println(aClass.getName());
            try {
                Object controller = aClass.getDeclaredConstructor().newInstance();
                System.out.println(controller);
                //check if class has annotation
                if(aClass.isAnnotationPresent(Controller.class)) {
                    System.out.println("Class has annotationAAAAAA");
                }
                if(aClass.equals(ControllerTest.class)) {
                    System.out.println("ispis gasa u mainu " + ((ControllerTest)controller).att);
                    System.out.println("ispis liste " + ((ControllerTest)controller).att.lista);
                    ((ControllerTest)controller).att.lista.add("dodao sam");
                    System.out.println("ispis liste " + ((ControllerTest)controller).att.lista);

                } else {
                    System.out.println("ispis gasa u mainu " + ((ControllerTest2)controller).att);
                    System.out.println("ispis liste " + ((ControllerTest2)controller).att.lista);
                    ((ControllerTest2)controller).att.lista.add("dodao sam");
                    System.out.println("ispis liste " + ((ControllerTest2)controller).att.lista);

                }

                System.out.println("registered new instanced controller: " + controller.getClass().getName());

                controllerMap.put(aClass.getName(), controller);


                for(Method method: aClass.getMethods()) {
                    if(method.isAnnotationPresent(Path.class)) {
                        Path requestMapping = method.getAnnotation(Path.class);
                        routes.put(requestMapping.value(), method);
                        System.out.println(requestMapping.value());
                    }
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
