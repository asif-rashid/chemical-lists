package com.example.chemicallists.catalog;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryListIdGenerator implements ListIdGenerator {

    private final AtomicLong counter = new AtomicLong(100);

    @Override
    public long nextId() {
        return counter.incrementAndGet();
    }
}
