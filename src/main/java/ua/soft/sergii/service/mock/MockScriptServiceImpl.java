package ua.soft.sergii.service.mock;

import ua.soft.sergii.container.ScriptsContainer;
import ua.soft.sergii.executor.ExecutionMode;
import ua.soft.sergii.executor.ScriptExecutor;
import ua.soft.sergii.executor.ScriptExecutorFactory;
import ua.soft.sergii.rest.bean.ScriptBean;
import ua.soft.sergii.rest.bean.ScriptStatus;
import ua.soft.sergii.service.ScriptService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MockScriptServiceImpl implements ScriptService {

    private final ScriptExecutorFactory scriptExecutorFactory;
    private final ScriptsContainer scriptsContainer;
    private final AtomicInteger scriptId;

    public MockScriptServiceImpl(ScriptsContainer scriptsContainer, ScriptExecutorFactory scriptExecutorFactory) {
        this.scriptsContainer = scriptsContainer;
        this.scriptExecutorFactory = scriptExecutorFactory;
        this.scriptId = new AtomicInteger();
    }

    @Override
    public int addScript(String accessToken, String scriptBody, ExecutionMode executionMode) {
        ScriptBean scriptBean = createScript(scriptBody, executionMode);
        scriptsContainer.addScript(accessToken, scriptBean);
        return scriptBean.getScriptId();
    }

    @Override
    public void executeScript(String accessToken, ScriptBean scriptBean) {
        scriptBean.getScriptExecutor().executeScript(accessToken, scriptBean);
    }

    @Override
    public List<ScriptBean> getAllScripts(String accessToken) {
        return scriptsContainer.getAllScripts(accessToken);
    }

    @Override
    public ScriptBean getScript(String accessToken, int scriptId) {
        return scriptsContainer.getScriptBean(accessToken, scriptId);
    }

    @Override
    public ScriptStatus getScriptStatus(String accessToken, int scriptId) {
        return scriptsContainer.getScriptBean(accessToken, scriptId).getScriptExecutor().getScriptStatus();
    }

    @Override
    public String scriptOutput(String accessToken, int scriptId) throws IOException {
        return scriptsContainer.getScriptBean(accessToken, scriptId).getCurrentScriptOutput();
    }

    @Override
    public void terminateScript(String accessToken, int scriptId) {
        ScriptBean scriptBean = getScript(accessToken, scriptId);
        scriptBean.getScriptExecutor().terminateScript(accessToken, scriptId);
    }

    @Override
    public void removeScript(String accessToken, int scriptId) {
        scriptsContainer.removeScript(accessToken, scriptId);
    }

    private ScriptExecutor getScriptExecutor(ExecutionMode executionMode) {
        return scriptExecutorFactory.getScriptExecutionService(executionMode);
    }

    private ScriptBean createScript(String scriptBody, ExecutionMode executionMode) {
        ScriptBean scriptBean = new ScriptBean(scriptId.getAndIncrement());
        scriptBean.setScriptBody(scriptBody);
        scriptBean.setScriptExecutor(getScriptExecutor(executionMode));
        return scriptBean;
    }
}
