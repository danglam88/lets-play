package com.LetsPlay.service;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final RateLimiter rateLimiter;

    public RateLimitService() {
        // Allow 50 requests per second
        this.rateLimiter = RateLimiter.create(50.0);
    }

    public boolean allowRequest() {
        // Try to acquire a permit, waiting for a maximum of 1 second
        return this.rateLimiter.tryAcquire(1, TimeUnit.SECONDS);
    }
}
