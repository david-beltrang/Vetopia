package com.vetopia.errors;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuenoNotFoundException.class)
    public String manejarDuenoNoEncontrado(DuenoNotFoundException e, Model model) {
        model.addAttribute("mensajeError", e.getMessage());
        return "error";
    }
}
