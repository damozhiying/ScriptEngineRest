package ua.soft.sergii.service;

import ua.soft.sergii.executor.ExecutionMode;
import ua.soft.sergii.rest.bean.ScriptBean;
import ua.soft.sergii.rest.bean.ScriptStatus;

import java.io.IOException;
import java.util.List;

public interface ScriptService {

    int addScript(String accessToken, String scriptBody, ExecutionMode executionMode);

    void executeScript(String accessToken, ScriptBean scriptBean);

    List<ScriptBean> getAllScripts(String accessToken);

    ScriptBean getScript(String accessToken, int scriptId);

    ScriptStatus getScriptStatus(String accessToken, int scriptId);

    String scriptOutput(String accessToken, int scriptId) throws IOException;

    void terminateScript(String accessToken, int scriptId);

    void removeScript(String accessToken, int scriptId);

}
