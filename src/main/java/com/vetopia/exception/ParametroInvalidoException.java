package com.vetopia.exception;

// Excepción personalizada para parámetros inválidos de la aplicación.
public class ParametroInvalidoException extends RuntimeException {

    public ParametroInvalidoException(String message) {
        super(message);
    }

    public ParametroInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}