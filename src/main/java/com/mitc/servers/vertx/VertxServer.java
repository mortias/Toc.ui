package com.mitc.servers.vertx;

import com.mitc.toc.config.Settings;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

public class VertxServer implements Runnable {

    private int port;
    private Vertx vertx;
    private HttpServer httpServer;
    private SockJSServer sockServer;

    public VertxServer(Settings settings) {

        vertx = VertxFactory.newVertx();
        port = settings.getVertxPort();

        // Let everything through
        JsonArray permitted = new JsonArray();
        permitted.add(new JsonObject());

        httpServer = vertx.createHttpServer();

        sockServer = vertx.createSockJSServer(httpServer);
        sockServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);

        vertx.eventBus().registerHandler("someaddress", new Handler<Message>() {
            @Override
            public void handle(Message event) {

                System.out.println("aa" + event.body().toString());

                JsonObject msg = new JsonObject();
                msg.putString("height", "550")
                        .putString("width", "560")
                        .putString("text", "zzzz")
                        .putString("skin", "cupertino");

                vertx.eventBus().publish("someaddress2", msg);
            }
        });

    }

    @Override
    public void run() {
        System.out.println("vertx server launched " + port );
        httpServer.listen(port);
    }

}

