package net.quepierts.papyri.exception;

import lombok.Getter;

@Getter
public abstract class ServiceException extends RuntimeException {
    private final String type;
    public ServiceException(String reason, String type) {
        super(reason);
        this.type = type;
    }

    public String getReason() {
        return "[" + type + "]: " + getMessage();
    }
}
