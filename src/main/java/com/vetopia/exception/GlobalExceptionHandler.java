package com.vetopia.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Manejo de excepciones: centraliza en un solo lugar la respuesta para
// los errores de la aplicación, evitando repetir try/catch en cada controlador.
@ControllerAdvice
public class GlobalExceptionHandler {

    // Recurso no encontrado como dueño o mascota con id inexistente).
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String manejarNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        model.addAttribute("mensajeError", ex.getMessage());
        return "error";
    }

    // Parámetro inválido como id nulo o vacío.
    @ExceptionHandler(ParametroInvalidoException.class)
    public String manejarParametroInvalido(ParametroInvalidoException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        model.addAttribute("mensajeError", ex.getMessage());
        return "error";
    }
}