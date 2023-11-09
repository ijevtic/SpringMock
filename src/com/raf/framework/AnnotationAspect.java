package com.raf.framework;


import com.raf.annotations.*;
import com.raf.server.RouteRegister;
import com.raf.util.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
public class AnnotationAspect {

    @Around("call(*.new(..)) || call(* *.newInstance(..)) ")
    public Object aroundObjectCreation(ProceedingJoinPoint jp) throws Throwable {

        Object constructedObject = jp.proceed();
        Class<?> constructedClass = constructedObject.getClass();
        if(!constructedClass.isAnnotationPresent(Bean.class) &&
        !constructedClass.isAnnotationPresent(Component.class) &&
        !constructedClass.isAnnotationPresent(Service.class) &&
        !constructedClass.isAnnotationPresent(Controller.class)) {

            return constructedObject;
        }

        Scope scope = Scope.SINGLETON;

        if (constructedClass.isAnnotationPresent(Bean.class)) {
            scope = constructedClass.getAnnotation(Bean.class).scope();
        }
        if (constructedClass.isAnnotationPresent(Component.class)) {
            scope = Scope.PROTOTYPE;
        }

        if (scope == Scope.SINGLETON && RouteRegister.diEngine.isSingletonCreated(constructedClass.getName())) {
            return RouteRegister.diEngine.getSingleton(constructedClass.getName());
        }

        // Retrieve fields and their annotations
        Field[] fields = constructedClass.getDeclaredFields();
        for (Field field : fields) {

            Class<?> fieldClass = field.getType();

            if(field.isAnnotationPresent(Autowired.class)) {

                if(fieldClass.isInterface()) {
                    Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                    System.out.println(fieldClass.getName());
                    if(qualifierAnnotation == null) {
                        System.out.println(constructedClass.getName() + " " + fieldClass.getName());
                        throw new Exception("No qualifier found for interface " + fieldClass.getName());
                    }
                    String qualifier = qualifierAnnotation.value();
                    fieldClass = RouteRegister.dependencyContainer.getImplementation(fieldClass, qualifier);
                }

                Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                if(autowiredAnnotation.verbose()) {
                    LocalDateTime now = LocalDateTime.now();
                    String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                    System.out.println(String.format("Initialized %s " +
                                    "%s in %s on %s with %d",
                            fieldClass.getName(),
                            field.getName(),
                            constructedClass.getName(),
                            formattedDateTime,
                            constructedObject.hashCode()));

                }
                field.setAccessible(true);
                Object object = fieldClass.newInstance();
                field.set(constructedObject, object);

            }
        }

        if(scope == Scope.SINGLETON) {
            RouteRegister.diEngine.addSingleton(constructedClass.getName(), constructedObject);
        }

        return constructedObject;
    }
}
