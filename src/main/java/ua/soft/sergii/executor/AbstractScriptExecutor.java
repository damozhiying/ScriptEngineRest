package ua.soft.sergii.executor;

import ua.soft.sergii.rest.bean.ScriptStatus;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public abstract class AbstractScriptExecutor implements ScriptExecutor {

    private static final String ENCODING = "UTF-8";
    protected final ScriptEngine scriptEngine;
    protected ScriptStatus scriptStatus;
    private static final String SCRIPT_ENGINE = "nashorn";
    private final ByteArrayOutputStream output;


    public AbstractScriptExecutor(ScriptEngineManager scriptEngineManager) {
        this.scriptEngine = scriptEngineManager.getEngineByName(SCRIPT_ENGINE);
        this.output = new ByteArrayOutputStream();
        this.scriptStatus = ScriptStatus.WAITING;
        try {
            this.scriptEngine.getContext().setWriter(new OutputStreamWriter(output, ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getCurrentOutput() throws IOException {
        try {
            scriptEngine.getContext().getWriter().flush();
            return output.toString(ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public ScriptStatus getScriptStatus() {
        return scriptStatus;
    }
}
