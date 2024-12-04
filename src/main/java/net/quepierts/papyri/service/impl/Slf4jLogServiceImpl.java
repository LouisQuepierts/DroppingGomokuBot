package net.quepierts.papyri.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.quepierts.papyri.service.LogService;

@Slf4j
public class Slf4jLogServiceImpl implements LogService {
    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        log.info(message, args);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    @Override
    public void error(String message, Object... args) {
        log.error(message, args);
    }

    @Override
    public void warning(String message) {
        log.warn(message);
    }

    @Override
    public void warning(String message, Throwable throwable) {
        log.warn(message, throwable);
    }

    @Override
    public void warning(String message, Object... args) {
        log.warn(message, args);
    }
}
