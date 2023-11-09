package com.raf.server;


import com.raf.annotations.Controller;
import com.raf.annotations.GET;
import com.raf.annotations.POST;
import com.raf.annotations.Path;
import com.raf.controller.*;
import com.raf.util.HttpMethod;
import com.raf.util.Pair;
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
    public static DependencyContainer dependencyContainer;
    public static DIEngine diEngine;

    public RouteRegister() {
        dependencyContainer = new DependencyContainer();
        diEngine = new DIEngine();
    }

    public void registerRoutes() {
        System.out.println("Registering routes...");
    }


    public void registerControllers() {

        dependencyContainer.addNewImplementation(Interface1.class, "impl1", TestAttributeClass.class);
        dependencyContainer.addNewImplementation(Interface1.class, "impl2", TestAttributeClass2.class);

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

                System.out.println("registered new instanced controller: " + controller.getClass().getName());
                if(controller instanceof ControllerTest) {
                    System.out.println(((ControllerTest) controller).att.getClass());
                }
                if(controller instanceof ControllerTest2) {
                    System.out.println(((ControllerTest2) controller).att.getClass());
                }

                diEngine.putController(aClass.getName(), controller);

                for(Method controllerMethod: aClass.getMethods()) {
                    if(controllerMethod.isAnnotationPresent(Path.class)) {
                        Path requestMapping = controllerMethod.getAnnotation(Path.class);
                        HttpMethod httpMethod = HttpMethod.GET;
                        if(!controllerMethod.isAnnotationPresent(GET.class) && !controllerMethod.isAnnotationPresent(POST.class)) {
                            //TODO error handling
                            throw new RuntimeException("Controller method must be annotated with either GET or POST");
                        }
                        if(controllerMethod.isAnnotationPresent(POST.class)) {
                            httpMethod = HttpMethod.POST;
                        }

                        diEngine.putRoute(httpMethod + " " + requestMapping.value(), new Pair<Object, Object>(controller, controllerMethod));
                        System.out.println(httpMethod + " " + requestMapping.value());
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
