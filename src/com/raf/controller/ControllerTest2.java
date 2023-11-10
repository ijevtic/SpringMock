package com.raf.controller;

import com.raf.annotations.*;
import com.raf.framework.request.Header;
import com.raf.framework.response.JsonResponse;
import com.raf.framework.response.Response;

@Controller
public class ControllerTest2 {

    @Autowired
    @Qualifier("impl2")
    public Interface1 att;

    public class Proba {
        public String ime = "test1";
        public String prezime = "test2";

    }
    @GET
    @Path("/users1")
    public Response testUsers() {
        Object o = new Proba();
        return new JsonResponse(o);
    }

    @GET
    @Path("/users2")
    public Response testUsers2(String s) {
        Proba o = new Proba();
        o.ime = s;
        return new JsonResponse(o);
    }
}