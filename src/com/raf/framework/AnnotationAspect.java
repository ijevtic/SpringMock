package com.raf.framework;


import com.raf.annotations.Autowired;
import com.raf.annotations.Bean;
import com.raf.annotations.Component;
import com.raf.annotations.Service;
import com.raf.server.RouteRegister;
import com.raf.util.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Aspect
public class AnnotationAspect {
    //pointcut for constructor call for all object of annotation Controller
    @Pointcut("execution((@com.raf.annotations.Controller *).new(..))")
    void controllerClass(){
    }

    @Pointcut("execution((@com.raf.annotations.Component *).new(..))")
    public void componentClass() {}

    @Pointcut("execution((@com.raf.annotations.Service *).new(..))")
    public void serviceClass() {}

    @Pointcut("execution((@com.raf.annotations.Bean *).new(..))")
    public void beanClass() {}


    @Around("controllerClass() || beanClass() || serviceClass() || componentClass()")
    public Object aroundObjectCreation(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        Object constructedObject = joinPoint.getTarget();

        Class<?> constructedClass = constructedObject.getClass();

        System.out.println("Class name: " + constructedClass.getSimpleName());

        System.out.println("Intercepting object creation of annotated class");

        Scope scope = Scope.SINGLETON;

        if (constructedClass.isAnnotationPresent(Bean.class)) {
            scope = constructedClass.getAnnotation(Bean.class).scope();
        }
        if (constructedClass.isAnnotationPresent(Component.class)) {
            scope = Scope.PROTOTYPE;
        }

        if (scope == Scope.SINGLETON && RouteRegister.singletonMap.containsKey(constructedClass.getName())) {
            System.out.println("ovo je singleton, vec bio!!!");
            System.out.println(RouteRegister.singletonMap.get(constructedClass.getName()));

            constructedObject = RouteRegister.singletonMap.get(constructedClass.getName());
//            deepcopy(constructedObject, RouteRegister.singletonMap.get(constructedClass.getName()));
            return RouteRegister.singletonMap.get(constructedClass.getName());
        }

        // Retrieve fields and their annotations
        Field[] fields = constructedClass.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("Field: " + field.getName());

            Class<?> fieldClass = field.getType();

            if(field.isAnnotationPresent(Autowired.class)) {
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
                Object object = checkForSingleton(fieldClass);
                if(object == null)
                    object = fieldClass.newInstance();
                field.set(constructedObject, object);

            }
        }

        if(scope == Scope.SINGLETON) {
            RouteRegister.singletonMap.put(constructedClass.getName(), constructedObject);
        }

        return constructedObject;
    }

    private void deepcopy(Object from, Object to) {
        if (from.getClass() != to.getClass()) {
            throw new IllegalArgumentException("Both objects must be of the same class");
        }

        Field[] fields = from.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(from);
                field.set(to, value);
                System.out.println("deepcopy: " + field.getName() + " "  + value);
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle exception as needed
            }
        }
    }

    private Object checkForSingleton(Class<?> clazz) {
        boolean isSingleton = false;
        if (clazz.isAnnotationPresent(Bean.class)) {
            Bean beanAnnotation = clazz.getAnnotation(Bean.class);
            if (beanAnnotation.scope() == Scope.SINGLETON)
                isSingleton = true;
            else
                return null;
        }
        if(clazz.isAnnotationPresent(Service.class))
            isSingleton = true;

        if (!isSingleton)
            return null;

        if (RouteRegister.singletonMap.containsKey(clazz.getName()))
            return RouteRegister.singletonMap.get(clazz.getName());

        return null;
    }
}
