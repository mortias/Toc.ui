package com.mitc.rest.server;

import com.mitc.util.Config;
import com.mitc.util.Settings;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import com.wordnik.swagger.servlet.config.ServletScanner;
import com.wordnik.swagger.servlet.listing.ApiDeclarationServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.FilterRegistration;

public class RESTServer {

    private static final Logger logger = LogManager.getLogger(RESTServer.class);

    private org.eclipse.jetty.server.Server jetty;

    public static Config config = Config.getInstance();
    private Settings settings = config.getSettings();

    private static RESTServer instance = null;

    public static RESTServer getInstance() {
        if (instance == null) {
            instance = new RESTServer();
        }
        return instance;
    }

    public RESTServer() {

        jetty = new org.eclipse.jetty.server.Server(settings.getPort());

        ServletHolder jersey = new ServletHolder(ServletContainer.class);
        jersey.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.mitc.rest.resources");
        jersey.setInitOrder(1);

        initSwagger();

        ServletContextHandler context = new ServletContextHandler(jetty, "/", ServletContextHandler.SESSIONS);
        context.addServlet(jersey, "/api/*");
        context.addServlet(ApiDeclarationServlet.class, "/api-docs/*");
        context.addServlet(DefaultServlet.class, "/*");

        final FilterRegistration.Dynamic apiOriginFilter
                = context.getServletContext().addFilter("ApiOriginFilter", ApiOriginFilter.class);

        apiOriginFilter.addMappingForUrlPatterns(null, false, "/*");

    }

    public void start() {
        try {
            jetty.start();
            jetty.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            jetty.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSwagger() {

        final BeanConfig config = new BeanConfig();
        config.setVersion("1.0.0");
        config.setBasePath("http://" + settings.getHost() + ":" + settings.getPort() + "/api");
        config.setResourcePackage("com.mitc.rest.resources");
        config.setScan(true);

        ServletScanner scanner = new ServletScanner();
        scanner.setResourcePackage("com.mitc.rest.resources");
        ScannerFactory.setScanner(scanner);

    }

}

