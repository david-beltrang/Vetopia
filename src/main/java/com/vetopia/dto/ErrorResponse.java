package com.vetopia.dto;

import java.time.LocalDateTime;

/**
 * Cuerpo de error que devuelven los endpoints REST cuando algo sale mal.
 * Se usa uno solo para todos los casos para que Angular sepa siempre qué
 * campos esperar, sin importar si el fallo fue un 400, un 404 o un 500.
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
