package com.raf.controller;

import com.raf.annotations.*;

@Controller
public class ControllerTest {

    @Autowired
    @Qualifier("impl1")
    public Interface1 att;

    @GET
    @Path("/users")
    public void testUsers() {

    }
}
