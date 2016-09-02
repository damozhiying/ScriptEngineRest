package ua.soft.sergii.executor;

import ua.soft.sergii.exception.ScriptTerminationException;
import ua.soft.sergii.exception.ServerException;
import ua.soft.sergii.rest.bean.ScriptBean;
import ua.soft.sergii.rest.bean.ScriptStatus;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;

public class BlockingScriptExecutor extends AbstractScriptExecutor {

    private static final int EXECUTION_TIMEOUT_IN_MILLIS = 10 * 1000;
    private static final int WAITING_TIME_PIECE_IN_MILLIS = 50;

    public BlockingScriptExecutor(ScriptEngineManager scriptEngineManager) {
        super(scriptEngineManager);
    }

    @Override
    public void terminateScript(String accessToken, int scriptId) {
        throw new ScriptTerminationException("Cannot terminate script, running in blocking mode");
    }

    @Override
    public void executeScript(String accessToken, ScriptBean scriptBean) {
        Thread scriptProcessingThread = new Thread(new ScriptExecutionThread(scriptBean.getScriptBody()));
        scriptProcessingThread.start();
        try {
            waitScriptExecutionFinish(scriptProcessingThread);
        } catch (InterruptedException e) {
            throw new ServerException(e);
        }
    }

    private void waitScriptExecutionFinish(Thread scriptProcessingThread) throws InterruptedException {
        int millisWaited = 0;
        while (scriptProcessingThread.isAlive() && millisWaited < EXECUTION_TIMEOUT_IN_MILLIS) {
            Thread.sleep(WAITING_TIME_PIECE_IN_MILLIS);
            millisWaited += WAITING_TIME_PIECE_IN_MILLIS;
        }
        if (scriptProcessingThread.isAlive()) {
            scriptProcessingThread.stop();
            scriptStatus = ScriptStatus.TERMINATED_TIMEOUT;
        }
    }

    private class ScriptExecutionThread extends Thread {

        private String script;

        private ScriptExecutionThread(String script) {
            this.script = script;
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
