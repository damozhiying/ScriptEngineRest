package ua.soft.sergii.service.mock;

import ua.soft.sergii.service.AccessTokenService;

import java.util.concurrent.atomic.AtomicInteger;

public class MockIntegerTokenService implements AccessTokenService<Integer> {

    private final AtomicInteger executorServiceId;

    public MockIntegerTokenService() {
        this.executorServiceId = new AtomicInteger();
    }

    @Override
    public Integer getNewAccessToken() {
        return executorServiceId.getAndIncrement();
    }
}
