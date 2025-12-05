package com.lurkerz.lupus.common;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }
}
