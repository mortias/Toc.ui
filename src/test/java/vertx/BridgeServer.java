package vertx;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

public class BridgeServer {

    public static synchronized void main(String[] args) throws Exception {

        Vertx vertx = VertxFactory.newVertx();
        HttpServer server = vertx.createHttpServer();

        JsonArray permitted = new JsonArray();
        permitted.add(new JsonObject()); // Let everything through

        ServerHook hook = new ServerHook();
        SockJSServer sockJSServer = vertx.createSockJSServer(server);
        sockJSServer.setHook(hook);
        sockJSServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);
        server.listen(9356);

        // Prevent the JVM from exiting
        System.in.read();

    }
}
