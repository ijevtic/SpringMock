package com.raf.server;


import com.google.gson.Gson;
import com.raf.framework.request.Header;
import com.raf.framework.request.Helper;
import com.raf.framework.request.Request;
import com.raf.framework.request.enums.Method;
import com.raf.framework.request.exceptions.RequestNotValidException;
import com.raf.framework.response.JsonResponse;
import com.raf.framework.response.Response;
import com.raf.util.Pair;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {

            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }
            Object requestData = RouteRegister.diEngine.findRoute(request.getMethod().toString(), request.getLocation());

            if(requestData == null) {
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type: text/html");
                out.println("Content-Length: 0");
                out.println("");
                in.close();
                out.close();
                socket.close();
                return;
            }

            Pair<Object, java.lang.reflect.Method> routeData = (Pair<Object, java.lang.reflect.Method>) requestData;
            Object controller = routeData.getFirst();
            java.lang.reflect.Method method = routeData.getSecond();

            // Invoke method
            System.out.println(method.getName());
            Object responseContent;
            HashMap<String, String> parameters = request.getParameters();
            Object[] methodArguments = new Object[parameters.size()];

            int index = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                methodArguments[index] = entry.getValue();
                index++;
            }

            // Invoking the method with the arguments
            responseContent = method.invoke(controller, methodArguments);
            Response response = (Response) responseContent;

            out.println(response.render());

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }
        String[] actionRow = command.split(" ");
        Method method = Method.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        if(route.contains("?"))
            route = route.substring(0, route.indexOf("?"));

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(Method.POST)) {
            int contentLength = Integer.parseInt(header.get("content-length"));
            char[] buff = new char[contentLength];
            in.read(buff, 0, contentLength);
            String parametersString = new String(buff);

            HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
            for (String parameterName : postParameters.keySet()) {
                parameters.put(parameterName, postParameters.get(parameterName));
            }
        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
