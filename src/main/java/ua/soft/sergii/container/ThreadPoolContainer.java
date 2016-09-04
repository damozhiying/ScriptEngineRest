package ua.soft.sergii.container;

import java.util.HashMap;
import java.util.Map;

public final class ThreadPoolContainer {

    private static final int POOL_SIZE = 3;
    private static final int EXECUTION_TIMEOUT_IN_MILLIS = 60 * 1000;
    private static final int WAITING_TIME_PIECE_IN_MILLIS = 100;

    private final Map<String, ThreadPool> executorServices;

    public ThreadPoolContainer() {
        this.executorServices = new HashMap<>();
    }

    public ThreadPool getThreadPool(String accessToken) {
        ThreadPool threadPool = executorServices.get(accessToken);
        if (threadPool == null) {
            threadPool = addNewThreadPool(accessToken);
        }
        return threadPool;
    }

    private ThreadPool addNewThreadPool(String accessToken) {
        ThreadPool threadPool = new ThreadPool(POOL_SIZE, EXECUTION_TIMEOUT_IN_MILLIS, WAITING_TIME_PIECE_IN_MILLIS);
        executorServices.put(accessToken, threadPool);
        return threadPool;
    }

}
