package com.mitc.rest.resources;

import com.mitc.Toc;
import com.mitc.dto.Settings;
import com.mitc.dto.SettingsForm;
import com.mitc.rest.server.RESTServer;
import com.mitc.rest.uri.RestURIConstants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.model.ParamQualifier;

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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Server information", httpMethod = "POST", response = SettingsForm.class,
            notes = "Use the following service to verify the server information, the same as the About " +
                    "JasperReports Server link in the user interface.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "This request should always succeed when the server is running and " +
                    "returns a structure containing the information in the requested ")})
    public Response postSettings(
             SettingsForm form){

        try {

            System.out.println("settings is called, height: " + form.getHeight() + ", width : " + form.getWidth());

            Toc.stage.setWidth(form.getWidth());
            Toc.stage.setHeight(form.getHeight());

            return Response.status(200)
                    .entity("settings is called, height: " + form.getHeight() + ", width : " + form.getWidth())
                    .build();
        } catch (Exception e) {
            logger.error("RestService error {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}