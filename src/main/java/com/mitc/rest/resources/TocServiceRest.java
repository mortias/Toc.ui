package com.mitc.rest.resources;

import com.mitc.dto.Settings;
import com.mitc.rest.uri.RestURIConstants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("toc")
@Api(value = "toc service", description = "Use the following service to change the settings of the TOC application.")
public class TocServiceRest {

    private static final Logger logger = LogManager.getLogger(TocServiceRest.class);

    @GET
    @Path(RestURIConstants.INFO)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Server information", httpMethod = "GET", response = Settings.class,
            notes = "Use the following service to verify the server information, the same as the About " +
                    "JasperReports Server link in the user interface.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "This request should always succeed when the server is running and " +
                    "returns a structure containing the information in the requested ")})
    public Response getAppInfo() {
        try {
            return Response.status(200).entity("zzzzzzzzzzz").build();
        } catch (Exception e) {
            logger.error("RestService error {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path(RestURIConstants.SETTINGS)
    @ApiOperation(
            value = "Server information", httpMethod = "POST", response = String.class,
            notes = "Use the following service to verify the server information, the same as the About " +
                    "JasperReports Server link in the user interface.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "This request should always succeed when the server is running and " +
                    "returns a structure containing the information in the requested ")})
    public Response addUser(
            @FormParam("height") int height,
            @FormParam("width") int width) {
        try {

            System.out.println("settings is called, height: " + height + ", width : " + width);

            return Response.status(200)
                    .entity("settings is called, height: " + height + ", width : " + width)
                    .build();
        } catch (Exception e) {
            logger.error("RestService error {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
