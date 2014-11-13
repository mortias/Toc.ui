package vertx;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

public class BridgeServer {

    public static synchronized void main(String[] args) throws Exception {

        Vertx vertx = VertxFactory.newVertx();

        JsonArray permitted = new JsonArray();
        permitted.add(new JsonObject()); // Let everything through

        HttpServer server = vertx.createHttpServer();

        SockJSServer sockServer = vertx.createSockJSServer(server);
        sockServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);
        server.listen(9356);

        vertx.eventBus().registerHandler("someaddress", new Handler<Message>() {
            @Override
            public void handle(Message event) {

                System.out.println("aa" + event.body().toString());

                JsonArray msg = new JsonArray();
                msg.add(new JsonObject().putString("height", "550"));
                msg.add(new JsonObject().putString("width", "560"));
                msg.add(new JsonObject().putString("text", "zzzz"));
                msg.add(new JsonObject().putString("skin", "cupertino"));

                vertx.eventBus().publish("someaddress2", msg);
            }
        });

        // Prevent the JVM from exiting
        System.in.read();


    }
}
