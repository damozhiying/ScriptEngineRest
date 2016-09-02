package ua.soft.sergii.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class IllegalExecutionModeException extends WebApplicationException {

    public IllegalExecutionModeException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(message).build());
    }
}
