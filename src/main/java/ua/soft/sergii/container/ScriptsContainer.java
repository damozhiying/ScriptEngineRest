package ua.soft.sergii.container;

import ua.soft.sergii.exception.ScriptNotFoundException;
import ua.soft.sergii.rest.bean.ScriptBean;
import ua.soft.sergii.rest.bean.ScriptStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScriptsContainer {

    private final Map<String, Map<Integer, ScriptBean>> scriptBeans;

    public ScriptsContainer() {
        this.scriptBeans = new HashMap<>();
    }

    public ScriptBean getScriptBean(String accessToken, int scriptId) {
        Map<Integer, ScriptBean> clientScriptBeans = getClientScripts(accessToken);
        return clientScriptBeans.get(scriptId);
    }

    public void addScript(String accessToken, ScriptBean scriptBean) {
        Map<Integer, ScriptBean> clientScriptBeans = scriptBeans.get(accessToken);
        if (clientScriptBeans == null) {
            clientScriptBeans = new HashMap<>();
        }
        clientScriptBeans.put(scriptBean.getScriptId(), scriptBean);
        scriptBeans.put(accessToken, clientScriptBeans);
    }

    public void removeScript(String accessToken, int scriptId) {
        ScriptBean scriptBean = getScriptBean(accessToken,scriptId);
        ScriptStatus scriptStatus = scriptBean.getScriptStatus();
        if (scriptStatus == ScriptStatus.PROCESSING || scriptStatus == ScriptStatus.WAITING) {
            scriptBean.getScriptExecutor().terminateScript(accessToken, scriptId);
        }
        scriptBeans.get(accessToken).remove(scriptId);
    }

    public List<ScriptBean> getAllScripts(String accessToken) {
        return new ArrayList<>(getClientScripts(accessToken).values());
    }

    private Map<Integer, ScriptBean> getClientScripts(String accessToken) {
        Map<Integer, ScriptBean> clientScriptBeans = scriptBeans.get(accessToken);
        if (clientScriptBeans == null) {
            throw new ScriptNotFoundException("Script(s) not found");
        }
        return clientScriptBeans;
    }
}
