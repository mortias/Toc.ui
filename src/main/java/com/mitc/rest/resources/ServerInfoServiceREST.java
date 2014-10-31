package com.mitc.rest.resources;

import com.mitc.Toc;
import com.mitc.dto.Settings;
import com.mitc.rest.uri.RestURIConstants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path(RestURIConstants.SERVER_INFO)
@Api(value = "Server info", description = "Use the following service to verify the server information.")
public class ServerInfoServiceREST {

    private static final Logger logger = LogManager.getLogger(ServerInfoServiceREST.class);

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Server information", httpMethod = "GET", response = Settings.class,
            notes = "Use the following service to verify the server information, the same as the About " +
                    "JasperReports Server link in the user interface.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "This request should always succeed when the server is running and " +
                    "returns a structure containing the information in the requested ")})
    public Response getServerInfo() throws NamingException {

        try {

            Settings settings = Toc.config.getSettings();

            System.out.println("hiiiiiiiiiiiiiiiiiiiiiiiiiiii");

            return Response.status(200).entity(settings).build();

        } catch (Exception e) {
            logger.error("RestService error {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
