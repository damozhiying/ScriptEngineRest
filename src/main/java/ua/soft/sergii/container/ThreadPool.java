package ua.soft.sergii.container;

import org.apache.commons.collections4.map.LinkedMap;
import ua.soft.sergii.exception.ServerException;
import ua.soft.sergii.executor.AsyncScriptExecutor;
import ua.soft.sergii.rest.bean.ScriptStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {

    private final int POOL_SIZE;

    private final LinkedMap<Integer, AsyncScriptExecutor.AsyncScriptThread> tasks = new LinkedMap<>();
    private final Map<Integer, AsyncScriptExecutor.AsyncScriptThread> currentlyRunning = new HashMap<>();
    private final AtomicInteger currentRunningThreadsNumber;

    ThreadPool(int maxThreadNumber) {
        this.POOL_SIZE = maxThreadNumber;
        this.currentRunningThreadsNumber = new AtomicInteger();
        new ServiceThread().start();
    }

    public void addTask(int scriptId, AsyncScriptExecutor.AsyncScriptThread task) {
        synchronized (tasks) {
            tasks.put(scriptId, task);
            tasks.notifyAll();
        }
    }

    public void terminateTask(int scriptId) {
        synchronized (currentlyRunning) {
            AsyncScriptExecutor.AsyncScriptThread task = currentlyRunning.remove(scriptId);
            if (task != null) {
                currentRunningThreadsNumber.decrementAndGet();
                stopThreadIfAlive(task);
            }
        }
    }

    private void stopThreadIfAlive(AsyncScriptExecutor.AsyncScriptThread task) {
        if (task.isAlive()) {
            task.stop();
            task.setStatus(ScriptStatus.TERMINATED_BY_CLIENT);
            synchronized (tasks) {
                tasks.notifyAll();
            }
        }
    }

    private class ServiceThread extends Thread {

        private ServiceThread() {
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                if (!isTasksAvailable()) {
                    continue;
                }
                startScriptExecution();
            }
        }

        private boolean isTasksAvailable() {
            synchronized (tasks) {
                boolean tasksAvailable = true;
                try {
                    if (tasks.size() == 0) {
                        tasks.wait();
                        tasksAvailable = false;
                    } else if (currentRunningThreadsNumber.get() >= POOL_SIZE) {
                        tasks.wait();
                        tasksAvailable = false;
                    }
                } catch (InterruptedException e) {
                    throw new ServerException(e);
                }
                return tasksAvailable;
            }
        }

        private void startScriptExecution() {
            synchronized (currentlyRunning) {
                Integer scriptId = tasks.firstKey();
                AsyncScriptExecutor.AsyncScriptThread scriptThread = tasks.remove(scriptId);
                if (scriptThread != null) {
                    currentlyRunning.put(scriptId, scriptThread);
                    new ScriptSupportingThread(scriptThread, scriptId).start();
                    currentRunningThreadsNumber.incrementAndGet();
                }
            }
        }
    }

    private class ScriptSupportingThread extends Thread {

        private static final int EXECUTION_TIMEOUT_IN_MILLIS = 60 * 1000;
        private static final int WAITING_TIME_PIECE_IN_MILLIS = 100;

        private AsyncScriptExecutor.AsyncScriptThread scriptThread;
        private int scriptId;

        private ScriptSupportingThread(AsyncScriptExecutor.AsyncScriptThread scriptThread, int scriptId) {
            this.scriptThread = scriptThread;
            this.scriptId = scriptId;
        }

        @Override
        public void run() {
            scriptThread.start();
            try {
                waitScriptExecutionFinish(scriptThread);
                synchronized (tasks) {
                    tasks.notifyAll();
                }
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
            synchronized (currentlyRunning) {
                stopIfStillAlive(scriptProcessingThread);
            }
        }

        private void stopIfStillAlive(Thread scriptProcessingThread) {
            if (scriptProcessingThread.isAlive()) {
                scriptProcessingThread.stop();
                scriptThread.setStatus(ScriptStatus.TERMINATED_TIMEOUT);
                currentlyRunning.remove(scriptId);
                currentRunningThreadsNumber.decrementAndGet();
            }
        }
    }

}