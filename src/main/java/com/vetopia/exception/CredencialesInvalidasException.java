package com.vetopia.exception;

// Excepción personalizada para credenciales de login inválidas (401).
public class CredencialesInvalidasException extends RuntimeException {

    // Constructor que recibe un mensaje de error
    public CredencialesInvalidasException(String message) {
        super(message);
    }

    // Constructor que recibe un mensaje de error y una causa otra excepción
    public CredencialesInvalidasException(String message, Throwable cause) {
        super(message, cause);
    }
}
