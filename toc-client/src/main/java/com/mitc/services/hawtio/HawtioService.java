package com.mitc.services.hawtio;

import com.mitc.services.vertx.VertxService;
import com.mitc.services.vertx.resources.Channel;
import com.mitc.toc.Settings;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vertx.java.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

public class HawtioService implements Executor {

    public HawtioService(Settings settings) {
        execute(new HawtioServer(settings));
    }

    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }

    // embedded server class
    private class HawtioServer implements Runnable {

        private org.eclipse.jetty.server.Server jetty;
        private String warPath = "hawtio-default-offline-1.4.30.war";

        public HawtioServer(Settings settings) {

            settings.setHawtio(true);

            try {

                System.setProperty("hawtio.authenticationEnabled", "false");

                jetty = new org.eclipse.jetty.server.Server(settings.getHawtioPort());

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

        private void sendMessage(String text) {

            // let the client know i'm starting up
            JsonObject replyMsg = new JsonObject();
            replyMsg.putString("action", "showHawtIoStatus");
            replyMsg.putString("text", text);

            VertxService.sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);

        }
    }
}

