package com.wj.study.rpc.protocol.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class HttpServer {
    public void start(int port) {
        Server server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        server.setHandler(handler);
        handler.addServlet(HttpRpcServlet.class, "/*");
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
