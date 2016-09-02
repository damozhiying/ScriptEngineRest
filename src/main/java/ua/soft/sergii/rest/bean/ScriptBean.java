package ua.soft.sergii.rest.bean;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;
import ua.soft.sergii.exception.ServerException;
import ua.soft.sergii.executor.ScriptExecutor;

import java.io.IOException;

public class ScriptBean {

    @JsonView(ScriptBean.class)
    private final int scriptId;
    @JsonIgnore
    private ScriptExecutor scriptExecutor;
    @JsonView(ScriptBean.class)
    private String scriptBody;

    public ScriptBean(int scriptId) {
        this.scriptId = scriptId;
    }

    public int getScriptId() {
        return scriptId;
    }

    public ScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }

    public void setScriptExecutor(ScriptExecutor scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    @JsonView(ScriptBean.class)
    public ScriptStatus getScriptStatus() {
        return scriptExecutor.getScriptStatus();
    }

    @JsonView(ScriptBean.class)
    public String getCurrentScriptOutput() {
        try {
            return scriptExecutor.getCurrentOutput();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    public String getScriptBody() {
        return scriptBody;
    }

    public void setScriptBody(String scriptBody) {
        this.scriptBody = scriptBody;
    }
}
