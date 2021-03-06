package com.mitc.services.hawtio;

import com.mitc.services.vertx.VertxService;
import com.mitc.services.vertx.resources.Channel;
import com.mitc.config.Settings;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vertx.java.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

@Component("HawtioService")
public class HawtioService implements Executor {

    @Autowired
    Settings settings;

    @Autowired
    VertxService vertxService;

    public HawtioService() {
    }

    public void init() {
        execute(new HawtioServer());
    }

    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }

    // embedded server class
    private class HawtioServer implements Runnable {

        private org.eclipse.jetty.server.Server jetty;
        private String artifact = "hawtio-default-offline.war";

        public HawtioServer() {

            settings.setHawtio(true);

            try {

                System.setProperty("hawtio.authenticationEnabled", "false");

                jetty = new org.eclipse.jetty.server.Server(settings.getHawtioPort());

                // Add Hawt.io
                WebAppContext hawtioWebappCtx = new WebAppContext();
                hawtioWebappCtx.setContextPath("/hawtio");
                hawtioWebappCtx.setWar(
                        new File("tools" + settings.getPathSep() + "hawtio" + settings.getPathSep() + artifact).getCanonicalPath());

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

            vertxService.sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);

        }
    }
}

