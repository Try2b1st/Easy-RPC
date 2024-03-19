package org.wgz.servicie.impl;

import io.vertx.core.Vertx;
import org.wgz.servicie.HttpService;
import org.wgz.servicie.HttpServerHandle;

public class VertxHttpService implements HttpService {
    public void doStart(int port) {
        //创建Vertx实例
        Vertx vertx = Vertx.vertx();

        //创建Http服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        //监听端口并处理请求
//        httpServer.requestHandler(httpServerRequest -> {
//            System.out.println("Received request : " + httpServerRequest.method() + " " + httpServerRequest.uri());
//
//            //响应
//            httpServerRequest.response()
//                    .putHeader("content-type", "text/plain")
//                    .end("Hello from Vert.x httpService ");
//        });

        httpServer.requestHandler(new HttpServerHandle());

        //启动Http服务器 并监听指定端口
        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Vert.x HttpService success listen port: " + port);
            } else {
                System.out.println("Failed to start HttpService : " + result.cause());
            }
        });
    }
}
