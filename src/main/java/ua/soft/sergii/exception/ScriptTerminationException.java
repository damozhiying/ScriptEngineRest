package ua.soft.sergii.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ScriptTerminationException extends WebApplicationException {
    public ScriptTerminationException(String message) {
        super(message, Response.status(Response.Status.CONFLICT).entity(message).build());
    }
}
