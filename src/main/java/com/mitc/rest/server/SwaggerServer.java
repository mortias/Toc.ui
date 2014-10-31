package com.mitc.rest.server;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jersey.config.JerseyJaxrsConfig;
import com.wordnik.swagger.servlet.config.ServletScanner;
import com.wordnik.swagger.servlet.listing.ApiDeclarationServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.FilterRegistration;

public class SwaggerServer {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);

        ServletHolder jersey = new ServletHolder(ServletContainer.class);
        jersey.setInitParameter("jersey.config.server.provider.packages",
                "com.wordnik.swagger.jaxrs.json,com.mitc.rest.resources");

        jersey.setInitParameter("jersey.config.server.provider.classnames",
                "com.wordnik.swagger.jersey.listing.ApiListingResourceJSON," +
                        "com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider," +
                        "com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider");
        jersey.setInitParameter("jersey.config.server.wadl.disableWadl", "true");
        jersey.setInitOrder(1);

        ServletHolder jersey2Config = new ServletHolder(JerseyJaxrsConfig.class);
        jersey2Config.setInitParameter("api.version", "1.0.0");
        jersey2Config.setInitParameter("swagger.api.basepath", "http://localhost:8080/api");
        jersey2Config.setInitOrder(2);

        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(jersey, "/api/*");
        context.addServlet(jersey2Config, "");

        initSwagger();

        context.addServlet(ApiDeclarationServlet.class, "/api-docs/*");

        final FilterRegistration.Dynamic apiOriginFilter
                = context.getServletContext().addFilter("ApiOriginFilter", ApiOriginFilter.class);

        apiOriginFilter.addMappingForUrlPatterns(null, false, "/*");

        server.start();
        server.join();
    }

    private static void initSwagger() {

        SwaggerConfig swaggerConfig = ConfigFactory.config();
        swaggerConfig.setBasePath("http://localhost:8080/api");
        swaggerConfig.setApiVersion("1.0.0");

        ServletScanner scanner = new ServletScanner();
        scanner.setResourcePackage("com.mitc.rest.resources");
        ScannerFactory.setScanner(scanner);

    }
/*


    package com.mitc.rest.server;

    import com.wordnik.swagger.config.ConfigFactory;
    import com.wordnik.swagger.config.ScannerFactory;
    import com.wordnik.swagger.config.SwaggerConfig;
    import com.wordnik.swagger.jaxrs.config.DefaultJaxrsConfig;
    import com.wordnik.swagger.servlet.config.ServletScanner;
    import com.wordnik.swagger.servlet.listing.ApiDeclarationServlet;
    import javafx.application.Application;
    import org.eclipse.jetty.server.Server;
    import org.eclipse.jetty.servlet.ServletContextHandler;
    import org.eclipse.jetty.servlet.ServletHolder;
    import org.glassfish.jersey.server.ServerProperties;
    import org.glassfish.jersey.servlet.ServletContainer;

    import javax.servlet.FilterRegistration;
    import javax.servlet.ServletContext;
    import javax.servlet.ServletRegistration;

    public class SwaggerServer {

        public static void main(String[] args) throws Exception {

            Server server = new Server(8080);
            ServletHolder jersey = new ServletHolder(ServletContainer.class);
            jersey.setInitParameter(
                    "jersey.config.server.provider.packages",
                    "com.wordnik.swagger.jaxrs.json,com.mitc.rest");

            jersey.setInitParameter("jersey.config.server.provider.classnames",
                    "com.wordnik.swagger.jersey.listing.ApiListingResourceJSON," +
                            "com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider," +
                            "com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider");

            jersey.setInitParameter("jersey.config.server.wadl.disableWadl", "true");
            jersey.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
            jersey.setInitOrder(1);

            initSwagger();

            ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

            context.addServlet(jersey, "/*");
            context.addServlet(ApiDeclarationServlet.class, "/api-docs/*");

            // initJersey(context.getServletContext());
            // initDefaultJaxrsConfig(context.getServletContext());
//        initBootstrap(context.getServletContext());

            final FilterRegistration.Dynamic apiOriginFilter
                    = context.getServletContext().addFilter("ApiOriginFilter", ApiOriginFilter.class);

            apiOriginFilter.addMappingForUrlPatterns(null, false, "/*");




            server.start();
            server.join();
        }

        private static void initSwagger() {

            SwaggerConfig swaggerConfig = ConfigFactory.config();
            swaggerConfig.setBasePath("http://localhost:8080/api");
            swaggerConfig.setApiVersion("1.0.0");

            ServletScanner scanner = new ServletScanner();
            scanner.setResourcePackage("com.mitc.rest.resources");
            ScannerFactory.setScanner(scanner);

        }




        private static String getApplicationName() {
            return "Toc";
        }

        private static String getApplicationClassName() {
            return Application.class.getName();
        }

        protected static void initJersey(ServletContext servletContext) {
            final ServletRegistration.Dynamic dispatcher = servletContext.addServlet("jersey", new ServletContainer());
            dispatcher.setInitParameter("javax.ws.rs.Application", getApplicationClassName());
            dispatcher.setInitParameter(ServerProperties.WADL_FEATURE_DISABLE, "true");
            dispatcher.setInitParameter(ServerProperties.APPLICATION_NAME, getApplicationName());
            dispatcher.addMapping("/*");
            dispatcher.setLoadOnStartup(1);
        }

        protected static void initDefaultJaxrsConfig(ServletContext servletContext) {
            final ServletRegistration.Dynamic dispatcher
                    = servletContext.addServlet("DefaultJaxrsConfig", DefaultJaxrsConfig.class.getName());
            dispatcher.setInitParameter("api.version", "1.0.0");
            //     dispatcher.setInitParameter("swagger.filter", ApiAuthorizationFilterImpl.class.getName());
            dispatcher.setLoadOnStartup(2);
        }

        protected static void initBootstrap(ServletContext servletContext) {
            final ServletRegistration.Dynamic dispatcher
                    = servletContext.addServlet("SwaggerBootstrap", jdk.nashorn.internal.runtime.linker.Bootstrap.class.getName());
            dispatcher.setLoadOnStartup(2);
        }

 */


}

