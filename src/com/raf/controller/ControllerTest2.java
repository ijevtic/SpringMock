package com.raf.controller;

import com.raf.annotations.Autowired;
import com.raf.annotations.Controller;
import com.raf.annotations.GET;
import com.raf.annotations.Path;

@Controller
public class ControllerTest2 {

    @Autowired
    public TestAttributeClass att;

    @GET
    @Path("/users1")
    public void testUsers() {

    }
}