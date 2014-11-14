package com.mitc.servers.hawtio;

import com.mitc.servers.vertx.Channel;
import com.mitc.servers.vertx.VertxServer;
import com.mitc.toc.Settings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vertx.java.core.json.JsonObject;

import java.io.File;
import java.io.IOException;

public class HawtioServer implements Runnable {

    private Server jetty;
    private String warPath = "hawtio-default-offline-1.4.30.war";

    public HawtioServer(Settings settings) {

        try {

            settings.setHawtio(true);
            System.setProperty("hawtio.authenticationEnabled", "false");

            jetty = new Server(settings.getHawtioPort());

            // Add Hawt.io
            WebAppContext hawtioWebappCtx = new WebAppContext();
            hawtioWebappCtx.setContextPath("/hawtio");
            hawtioWebappCtx.setWar(
                    new File("tools" + settings.getPathSep() + "hawtio" + settings.getPathSep() + warPath).getCanonicalPath());

            // let the client know i'm starting up
            sendMessage("Deploying war..");

            jetty.setHandler(hawtioWebappCtx);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {

            jetty.join();
            jetty.start();

            sendMessage("Connected");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String text){

        // let the client know i'm starting up
        JsonObject replyMsg = new JsonObject();
        replyMsg.putString("action", "showHawtIoStatus");
        replyMsg.putString("text", text);

        VertxServer.sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);

    }

}

