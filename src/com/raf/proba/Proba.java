package com.raf.proba;

import com.raf.annotations.Controller;
import com.raf.annotations.TestAnnotation;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class Proba {
    public static void main(String[] args) {

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage("org.example")
                        .filterInputsBy(new FilterBuilder().includePackage("org.example")));
//        Reflections reflections = new Reflections("org.example.proba");

        Set<Class<?>> singletons =
                reflections.get(TypesAnnotated.with(Controller.class).asClass());
        System.out.println(singletons.size());
        for (Class<?> aClass : singletons) {
            System.out.println(aClass.getName());
        }

//        metoda(null);
//        metoda(null);
        //Objekat o = new Objekat();
    }

    @TestAnnotation
    static void metoda(String[] s) {
        System.out.println("hello metoda");
    }
}
