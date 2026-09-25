package com.vetopia.exception;

// Excepción personalizada para cuando un recurso (dueño, mascota, tratamiento,
// droga, veterinario, admin) no existe en la base de datos.
public class RecursoNoEncontradoException extends RuntimeException {

    // Constructor que recibe un mensaje de error
    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    // Constructor que recibe un mensaje de error y una causa otra excepción
    public RecursoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}