package ua.soft.sergii.executor;

import ua.soft.sergii.rest.bean.ScriptBean;
import ua.soft.sergii.rest.bean.ScriptStatus;

import java.io.IOException;

public interface ScriptExecutor {

    void executeScript(String accessToken, ScriptBean scriptBean);

    ScriptStatus getScriptStatus();

    String getCurrentOutput() throws IOException;

    void terminateScript(String accessToken, int scriptId);
}
