package com.raf.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException {

        RouteRegister routeRegister = new RouteRegister();
        routeRegister.registerControllers();

//        for(String key : RouteRegister.controllerMap.keySet()) {
//            System.out.println(key);
//            Object controller = RouteRegister.controllerMap.get(key);
//            ControllerTest controllerTest = (ControllerTest) controller;
//            System.out.println(controllerTest.gas);
//        }

        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:"+TCP_PORT);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}