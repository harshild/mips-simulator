package com.harshild.mips.exception;

public class ConfigurationReadErrorException extends Exception {
    private final String message;
    public ConfigurationReadErrorException(String message) {
        this.message = message;
    }
}
