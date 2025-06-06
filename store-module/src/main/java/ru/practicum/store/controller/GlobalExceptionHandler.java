package ru.practicum.store.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<Rendering> handleValidationError(Exception ex) {
        return Mono.just(
                Rendering.view("main")
                        .modelAttribute("validationError", ex)
                        .build()
        );
    }
}