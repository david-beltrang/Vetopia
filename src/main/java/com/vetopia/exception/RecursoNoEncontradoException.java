package com.vetopia.exception;

// Excepción personalizada para cuando un recurso (dueño, mascota, tratamiento,
// droga, veterinario, admin) no existe en la base de datos.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    public RecursoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}