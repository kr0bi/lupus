package com.lurkerz.lupus.common;

public class BadRequestException extends DomainException {
    public BadRequestException(String message) {
        super(message);
    }
}
