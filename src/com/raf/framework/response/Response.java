package com.raf.framework.response;


import com.raf.framework.request.Header;

public abstract class Response {
    protected Header header;

    public Response() {
        this.header = new Header();
    }

    public abstract String render();
}
