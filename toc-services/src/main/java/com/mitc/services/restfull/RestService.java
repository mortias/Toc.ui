package com.mitc.services.restfull;

import com.mitc.services.restfull.filters.CORSFilter;
import com.mitc.config.Settings;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import com.wordnik.swagger.servlet.config.ServletScanner;
import com.wordnik.swagger.servlet.listing.ApiDeclarationServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.FilterRegistration;
import java.util.concurrent.Executor;

@Component("RestService")
public class RestService implements Executor {

    @Autowired
    Settings settings;

    public RestService() {
    }

    public void init() {
        execute(new RestServer());
    }

    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }

    // embedded server class
    private class RestServer implements Runnable {

        private Server jetty;

        public RestServer() {

            jetty = new Server(settings.getRestPort());

            ServletHolder jersey = new ServletHolder(ServletContainer.class);
            jersey.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.mitc.services.restfull.resources");
            jersey.setInitOrder(1);

            // Init Swagger
            BeanConfig config = new BeanConfig();
            config.setVersion("1.0.0");
            config.setBasePath("http://" + settings.getHost() + ":" + settings.getRestPort() + "/api");
            config.setResourcePackage("com.mitc.services.restfull.resources");
            config.setScan(true);

            ServletScanner scanner = new ServletScanner();
            scanner.setResourcePackage("com.mitc.services.restfull.resources");
            ScannerFactory.setScanner(scanner);
            // --

            ServletContextHandler context = new ServletContextHandler(jetty, "/", ServletContextHandler.SESSIONS);
            context.addServlet(jersey, "/api/*");
            context.addServlet(ApiDeclarationServlet.class, "/api-docs/*");
            context.addServlet(DefaultServlet.class, "/*");

            final FilterRegistration.Dynamic apiOriginFilter
                    = context.getServletContext().addFilter("ApiOriginFilter", CORSFilter.class);

            apiOriginFilter.addMappingForUrlPatterns(null, false, "/*");

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
}

