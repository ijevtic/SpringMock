package com.raf.controller;

import com.raf.annotations.*;

@Controller
public class ControllerTest2 {

    @Autowired
    @Qualifier("impl2")
    public Interface1 att;

    @GET
    @Path("/users1")
    public void testUsers() {

    }
}