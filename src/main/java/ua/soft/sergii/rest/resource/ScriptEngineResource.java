package ua.soft.sergii.rest.resource;

import ua.soft.sergii.ApplicationConstants;
import ua.soft.sergii.exception.IllegalExecutionModeException;
import ua.soft.sergii.exception.ScriptNotFoundException;
import ua.soft.sergii.exception.ServerException;
import ua.soft.sergii.executor.ExecutionMode;
import ua.soft.sergii.rest.bean.ScriptBean;
import ua.soft.sergii.service.ScriptService;
import ua.soft.sergii.util.JsonParseUtil;

import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/scripts")
public class ScriptEngineResource {

    private ScriptService scriptService;

    @Context
    private void setScriptService(ServletContext servletContext) {
        scriptService =
                (ScriptService) servletContext.getAttribute(ApplicationConstants.SCRIPT_SERVICE);
    }

    @GET
    @Produces("application/json")
    public Response getAllScripts(@QueryParam(ApplicationConstants.ACCESS_TOKEN) String accessToken) throws ScriptException, IOException {
        List<ScriptBean> scriptBeans = scriptService.getAllScripts(accessToken);
        return Response.status(Response.Status.OK).entity(getJsonStringScriptBean(scriptBeans, ScriptBean.class)).build();
    }

    @POST
    public Response executeScript(@QueryParam(ApplicationConstants.EXECUTION_MODE) String executionModeName,
                                  @QueryParam(ApplicationConstants.ACCESS_TOKEN) String accessToken,
                                  @Context HttpServletRequest request,
                                  String scriptBody) throws IOException, InterruptedException, ScriptException {

        ExecutionMode executionMode = getExecutionMode(executionModeName);
        ScriptBean scriptBean = executeNewScript(accessToken, scriptBody, executionMode);
        try {
            switch (executionMode) {
                default:
                case BLOCKING:
                    return Response
                            .created(new URI(request.getRequestURI() + "/" + scriptBean.getScriptId()))
                            .status(Response.Status.OK)
                            .entity(getJsonStringScriptBean(scriptBean, ScriptBean.class))
                            .build();
                case ASYNC:
                    return Response
                            .created(new URI(request.getRequestURI() + "/" + scriptBean.getScriptId()))
                            .status(Response.Status.ACCEPTED)
                            .build();
            }
        } catch (URISyntaxException e) {
            throw new ServerException(e);
        }
    }

    @PUT
    @Path("/{scriptId}")
    public Response terminateScript(@PathParam("scriptId") int scriptId,
                                    @QueryParam(ApplicationConstants.ACCESS_TOKEN) String accessToken) {
        scriptService.terminateScript(accessToken, scriptId);
        return Response.status(Response.Status.OK).entity("Script " + scriptId + " was terminated").build();
    }

    @DELETE
    @Path("/{scriptId}")
    public Response removeScript(@PathParam("scriptId") int scriptId,
                                 @QueryParam(ApplicationConstants.ACCESS_TOKEN) String accessToken) {
        scriptService.removeScript(accessToken, scriptId);
        return Response.status(Response.Status.OK).entity("Script " + scriptId + " was removed").build();
    }

    @GET
    @Path("/{scriptId}")
    @Produces("application/json")
    public Response getScriptBean(@PathParam("scriptId") int scriptId,
                                  @QueryParam(ApplicationConstants.ACCESS_TOKEN) String accessToken) throws IOException {
        ScriptBean scriptBean = scriptService.getScript(accessToken, scriptId);
        if (scriptBean == null) {
            throw new ScriptNotFoundException("Script with id = " + scriptId + " not found");
        }
        return Response.status(Response.Status.OK).entity(getJsonStringScriptBean(scriptBean, ScriptBean.class)).build();
    }

    @GET
    @Path("/{scriptId}/status")
    public Response getScriptExecutionStatus(@PathParam("scriptId") int scriptId,
                                             @QueryParam(ApplicationConstants.ACCESS_TOKEN) String accessToken) {
        ScriptBean scriptBean = scriptService.getScript(accessToken, scriptId);
        return Response.status(Response.Status.OK).entity(scriptBean.getScriptStatus().toString()).build();
    }

    private ScriptBean executeNewScript(String accessToken, String scriptBody, ExecutionMode executionMode) throws IOException {
        int scriptId = scriptService.addScript(accessToken, scriptBody, executionMode);
        ScriptBean scriptBean = scriptService.getScript(accessToken, scriptId);
        scriptService.executeScript(accessToken, scriptBean);
        return scriptBean;
    }

    private String getJsonStringScriptBean(Object objectToConvert, Class<?> jsonView) throws IOException {
        return JsonParseUtil.getJsonString(objectToConvert, jsonView);
    }

    private ExecutionMode getExecutionMode(String executionModeName) {
        if (executionModeName == null) {
            return ExecutionMode.BLOCKING;
        } else {
            return parseExecutionModeValue(executionModeName);
        }
    }

    private ExecutionMode parseExecutionModeValue(String executionModeName) {
        ExecutionMode executionMode;
        try {
            executionMode = ExecutionMode.valueOf(executionModeName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalExecutionModeException("Wrong script execution mode " + executionModeName);
        }
        return executionMode;
    }
}
