package ua.soft.sergii.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ScriptNotFoundException extends WebApplicationException {

    public ScriptNotFoundException(String message) {
        super(message, Response.status(Response.Status.NOT_FOUND).entity(message).build());
    }
}
