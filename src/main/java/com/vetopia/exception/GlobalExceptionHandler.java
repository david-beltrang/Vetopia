package com.vetopia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

// Manejo GLOBAL de excepciones: centraliza en un solo lugar la respuesta para
// los errores comunes de la aplicación, evitando repetir try/catch en cada
// controlador.
@ControllerAdvice
public class GlobalExceptionHandler {

    // Recurso no encontrado (ej. dueño/mascota con id inexistente) -> 404.
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView manejarNoEncontrado(RecursoNoEncontradoException excepcion) {
        ModelAndView mav = new ModelAndView("error/not-found");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("mensaje", excepcion.getMessage());
        return mav;
    }

    // Argumento inválido que escapa de la validación local de los controllers.
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView manejarArgumentoInvalido(IllegalArgumentException excepcion) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("mensaje", excepcion.getMessage());
        return mav;
    }

    // Un id no numérico en la URL (ej. /detalle?id=abc).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView manejarIdNoNumerico(MethodArgumentTypeMismatchException excepcion) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("mensaje", "El identificador suministrado no es válido.");
        return mav;
    }
}