package com.mitc.servers.rest.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/toc")
@Api(value = "Toc service", description = "Use the following service to change the settings of the TOC application.")
public class TocServiceRest {

    private static final Logger logger = LogManager.getLogger(TocServiceRest.class);

    @GET
    @Path(RestURIConstants.INFO)
    @Produces(MediaType.TEXT_HTML)
    @ApiOperation(
            value = "Application information", httpMethod = "GET", response = String.class,
            notes = "Use the following service to get the server version.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "This request should always succeed when the server is running and " +
                    "returns a structure containing the information in the requested ")})
    public Response getAppInfo() {
        try {
            return Response.status(200).entity("Toc 0.0.1").build();
        } catch (Exception e) {
            logger.error("RestService error {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
