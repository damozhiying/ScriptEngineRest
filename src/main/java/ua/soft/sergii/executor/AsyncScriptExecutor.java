package ua.soft.sergii.executor;

import ua.soft.sergii.container.ThreadPoolContainer;
import ua.soft.sergii.exception.ScriptTerminationException;
import ua.soft.sergii.exception.ServerException;
import ua.soft.sergii.rest.bean.ScriptBean;
import ua.soft.sergii.rest.bean.ScriptStatus;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;

public class AsyncScriptExecutor extends AbstractScriptExecutor {

    private ThreadPoolContainer threadPoolContainer;

    public AsyncScriptExecutor(ScriptEngineManager scriptEngineManager,
                               ThreadPoolContainer threadPoolContainer) {
        super(scriptEngineManager);
        this.threadPoolContainer = threadPoolContainer;
    }

    @Override
    public void executeScript(String accessToken, ScriptBean scriptBean) {
        threadPoolContainer.getThreadPool(accessToken)
                .addTask(scriptBean.getScriptId(), new AsyncScriptThread(scriptBean.getScriptBody()));
    }

    @Override
    public void terminateScript(String accessToken, int scriptId) {
        boolean canBeTerminated = isScriptCanBeTerminated();
        threadPoolContainer.getThreadPool(accessToken).terminateTask(scriptId, scriptStatus);
        proceedIfNotTerminated(scriptId, canBeTerminated);
    }

    private boolean isScriptCanBeTerminated() {
        boolean canBeTerminated = false;
        if (scriptStatus == ScriptStatus.PROCESSING) {
            canBeTerminated = true;
        }
        return canBeTerminated;
    }

    private void proceedIfNotTerminated(int scriptId, boolean canBeTerminated) {
        if (canBeTerminated) {
            if (scriptStatus != ScriptStatus.TERMINATED_BY_CLIENT) {
                throw new ScriptTerminationException("Cannot terminate script with id = " + scriptId);
            }
        }
    }

    public class AsyncScriptThread extends Thread {

        private String script;

        private AsyncScriptThread(String script) {
            this.script = script;
        }

        public void setStatus(ScriptStatus status) {
            scriptStatus = status;
        }

        @Override
        public void run() {
            try {
                startProcessing();
            } catch (ScriptException e) {
                handleScriptException(e);
                return;
            }
            scriptStatus = ScriptStatus.FINISHED;
        }

        private void startProcessing() throws ScriptException {
            scriptStatus = ScriptStatus.PROCESSING;
            scriptEngine.eval(script);
        }

        private void handleScriptException(ScriptException e) {
            try {
                scriptStatus = ScriptStatus.TERMINATED_WITH_EXCEPTION;
                scriptEngine.getContext().getWriter().write(e.toString());
            } catch (IOException ex) {
                throw new ServerException(ex);
            }
        }

    }

}
