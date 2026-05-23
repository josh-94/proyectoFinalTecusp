package com.grupo10.orders.infrastructure.adapter.in.rest;

import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import com.grupo10.orders.domain.exception.TransicionEstadoInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ProblemDetail handleNotFound(PedidoNoEncontradoException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Pedido no encontrado");
        pd.setType(URI.create("urn:problem:pedido-no-encontrado"));
        return pd;
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ProblemDetail handleTransicion(TransicionEstadoInvalidaException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Transición de estado inválida");
        pd.setType(URI.create("urn:problem:transicion-invalida"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
        pd.setTitle("Validación fallida");
        pd.setType(URI.create("urn:problem:validacion"));
        pd.setProperty("errores", ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList());
        return pd;
    }
}
