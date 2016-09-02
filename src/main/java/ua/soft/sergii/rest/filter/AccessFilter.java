package ua.soft.sergii.rest.filter;

import ua.soft.sergii.ApplicationConstants;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.regex.Pattern;

@Provider
@PreMatching
public class AccessFilter implements ContainerRequestFilter{

    private static final String accessPattern = ".*/access[\\S]*";

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String requestURI = containerRequestContext.getUriInfo().getRequestUri().getPath();
        if (!Pattern.matches(accessPattern, requestURI)) {
            String accessToken = servletRequest.getParameter(ApplicationConstants.ACCESS_TOKEN);
            if (accessToken == null) {
                containerRequestContext.abortWith(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity("Access denied. Get access token first or send it in the query params")
                                .build());
            }

        }
    }
}
