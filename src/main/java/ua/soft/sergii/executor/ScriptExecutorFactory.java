package ua.soft.sergii.executor;

import ua.soft.sergii.container.ThreadPoolContainer;

import javax.script.ScriptEngineManager;
import javax.ws.rs.core.Context;


public class ScriptExecutorFactory {

    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    private final ThreadPoolContainer threadPoolContainer;

    public ScriptExecutorFactory(ThreadPoolContainer threadPoolContainer) {
        this.threadPoolContainer = threadPoolContainer;
    }

    @Context
    public ScriptExecutor getScriptExecutionService(ExecutionMode executionMode) {
        switch (executionMode) {
            case ASYNC:
                return new AsyncScriptExecutor(scriptEngineManager, threadPoolContainer);
            case BLOCKING:
            default:
                return new BlockingScriptExecutor(scriptEngineManager);
        }
    }

}
