package ua.soft.sergii.rest.resource;

import ua.soft.sergii.ApplicationConstants;
import ua.soft.sergii.service.AccessTokenService;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/access")
public class AccessTokenResource {

    private AccessTokenService accessTokenService;

    @Context
    private void setExecutorServiceContainer(ServletContext servletContext) {
        accessTokenService =
                (AccessTokenService) servletContext.getAttribute(ApplicationConstants.ACCESS_TOKEN_SERVICE);
    }

    @GET
    public Response getAccessToken() {
        return Response.status(200).entity(accessTokenService.getNewAccessToken()).build();
    }

}
