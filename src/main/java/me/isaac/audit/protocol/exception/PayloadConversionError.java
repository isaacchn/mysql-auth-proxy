package me.isaac.audit.protocol.exception;

public class PayloadConversionError extends RuntimeException {
    public PayloadConversionError(String message) {
        super(message);
    }
}
