package com.lsf.ironbus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationLifecycleLogger {

    @EventListener
    public void onShutdown(ContextClosedEvent event) {
        log.info(
            "Application shutdown initiated; waiting for active requests to complete"
        );
    }
}