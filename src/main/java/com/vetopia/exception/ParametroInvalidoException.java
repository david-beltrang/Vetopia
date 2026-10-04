package com.vetopia.exception;

// Excepción personalizada para parámetros inválidos de la aplicación.
public class ParametroInvalidoException extends RuntimeException {

    // Constructor que recibe un mensaje de error
    public ParametroInvalidoException(String message) {
        super(message);
    }

    // Constructor que recibe un mensaje de error y una causa otra excepción
    public ParametroInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}