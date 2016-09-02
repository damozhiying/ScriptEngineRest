package ua.soft.sergii.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ServerException extends WebApplicationException {

    public ServerException(Throwable cause) {
        super(cause, Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(cause.getMessage()).build());
    }

}
