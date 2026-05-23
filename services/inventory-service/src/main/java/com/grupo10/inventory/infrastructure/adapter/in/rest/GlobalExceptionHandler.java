package com.grupo10.inventory.infrastructure.adapter.in.rest;

import com.grupo10.inventory.domain.exception.LoteNoEncontradoException;
import com.grupo10.inventory.domain.exception.ProductoNoEncontradoException;
import com.grupo10.inventory.domain.exception.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockInsuficienteException.class)
    public ProblemDetail handleStockInsuficiente(StockInsuficienteException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Stock insuficiente");
        pd.setProperty("disponible", ex.getDisponible());
        pd.setProperty("solicitado", ex.getSolicitado());
        return pd;
    }

    @ExceptionHandler({LoteNoEncontradoException.class, ProductoNoEncontradoException.class})
    public ProblemDetail handleNotFound(RuntimeException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Recurso no encontrado");
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
        pd.setTitle("Validación fallida");
        pd.setProperty("errores", ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList());
        return pd;
    }
}
