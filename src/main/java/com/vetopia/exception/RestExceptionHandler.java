package com.vetopia.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vetopia.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejo de excepciones para los controladores REST (paquete com.vetopia.rest).
 *
 * Este advice es un
 * agregado para que, cuando algo falle, Angular reciba JSON con el código HTTP
 * correcto en lugar de una página de error HTML. Al acotarlo con basePackages,
 * el GlobalExceptionHandler sigue encargándose de los controladores con vistas por se decide seguir con springboot.
 *
 * Gracias a que el error sale como excepción, los controladores REST no llevan
 * try/catch: Spring elige aquí el método según el tipo y arma la respuesta.
 */
@RestControllerAdvice(basePackages = "com.vetopia.rest")
public class RestExceptionHandler {

    // 404 Not Found: la fila no existe en la BD (dueño, mascota, tratamiento,
    // droga, veterinario o administrador con ese id). Nace en la capa service.
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex,
            HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 400 Bad Request: la petición llegó con datos que la capa service rechaza
    // (id nulo, campos obligatorios sin enviar, relación sin seleccionar...).
    @ExceptionHandler(ParametroInvalidoException.class)
    public ResponseEntity<ErrorResponse> manejarParametroInvalido(ParametroInvalidoException ex,
            HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // 500: red de seguridad para lo que no anticipamos, de modo que el cliente
    // reciba siempre el mismo formato de error.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(Exception ex, HttpServletRequest request) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    // Arma el cuerpo y el código de una sola vez para no repetir el armado en
    // cada método de arriba.
    private ResponseEntity<ErrorResponse> construir(HttpStatus estado, String mensaje, HttpServletRequest request) {
        ErrorResponse cuerpo = new ErrorResponse(
                LocalDateTime.now(),
                estado.value(),
                estado.getReasonPhrase(),
                mensaje,
                request.getRequestURI());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
