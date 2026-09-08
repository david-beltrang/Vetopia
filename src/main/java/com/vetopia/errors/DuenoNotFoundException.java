package com.vetopia.errors;

public class DuenoNotFoundException extends RuntimeException {

    public DuenoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
