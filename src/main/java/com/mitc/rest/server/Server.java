package com.mitc.rest.server;

import com.mitc.dto.Settings;
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

public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private org.eclipse.jetty.server.Server jetty;
    private Settings settings;

    public Server(int port) {

        jetty = new org.eclipse.jetty.server.Server(port);

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
        config.setResourcePackage("com.mitc.rest.resources");
        config.setScan(true);

        ServletScanner scanner = new ServletScanner();
        scanner.setResourcePackage("com.mitc.rest.resources");
        ScannerFactory.setScanner(scanner);

    }

}

