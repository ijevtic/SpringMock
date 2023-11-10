package com.raf.controller;

import com.raf.annotations.*;
import com.raf.framework.response.JsonResponse;
import com.raf.framework.response.Response;

@Controller
public class ControllerTest {

    @Autowired
    @Qualifier("impl1")
    public Interface1 att;

    @GET
    @Path("/users")
    public void testUsers(String s) {

    }

    @POST
    @Path("/users")
    public Response testUsers2() {
        Response r = new JsonResponse("proba");
        return r;
    }
}
