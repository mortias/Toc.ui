package com.mitc.servers.hawtio;

import com.mitc.toc.config.Settings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;

public class HawtioServer implements Runnable {

    private Server jetty;
    private String warPath = "hawtio-default-offline-1.4.30.war";

    public HawtioServer(Settings settings) {

        try {

            System.setProperty("hawtio.authenticationEnabled", "false");
            jetty = new Server(settings.getHawtioPort());

            // Add Hawt.io
            WebAppContext hawtioWebappCtx = new WebAppContext();
            hawtioWebappCtx.setContextPath("/hawtio");
            hawtioWebappCtx.setWar(
                    new File("tools"+settings.getPathSep()+"hawtio"+settings.getPathSep()+warPath).getCanonicalPath());

            jetty.setHandler(hawtioWebappCtx);

        } catch (IOException e) {
            e.printStackTrace();
        };

    }

    @Override
    public void run() {
        try {
            jetty.join();
            jetty.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

