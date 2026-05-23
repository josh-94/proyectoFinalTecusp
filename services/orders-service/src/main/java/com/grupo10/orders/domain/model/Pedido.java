package com.grupo10.orders.domain.model;

import com.grupo10.orders.domain.exception.TransicionEstadoInvalidaException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Pedido {

    private final String id;
    private final String numeroPedido;
    private final String solicitadoPor;
    private final String hospitalDestino;
    private final List<LineaDePedido> lineas;
    private EstadoPedido estado;
    private String motivoRechazo;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Pedido(String id, String numeroPedido, String solicitadoPor, String hospitalDestino,
                  List<LineaDePedido> lineas, EstadoPedido estado, String motivoRechazo,
                  Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.numeroPedido = numeroPedido;
        this.solicitadoPor = solicitadoPor;
        this.hospitalDestino = hospitalDestino;
        this.lineas = Collections.unmodifiableList(lineas);
        this.estado = estado;
        this.motivoRechazo = motivoRechazo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public static Pedido crear(String solicitadoPor, String hospitalDestino, List<LineaDePedido> lineas) {
        Instant now = Instant.now();
        String id = UUID.randomUUID().toString();
        String numero = "PED-" + id.substring(0, 8).toUpperCase();
        return new Pedido(id, numero, solicitadoPor, hospitalDestino,
                lineas, EstadoPedido.PENDIENTE_STOCK, null, now, now);
    }

    public void confirmar() {
        if (estado != EstadoPedido.PENDIENTE_STOCK) {
            throw new TransicionEstadoInvalidaException(estado, EstadoPedido.CONFIRMADO);
        }
        this.estado = EstadoPedido.CONFIRMADO;
        this.actualizadoEn = Instant.now();
    }

    public void rechazar(String motivo) {
        if (estado != EstadoPedido.PENDIENTE_STOCK) {
            throw new TransicionEstadoInvalidaException(estado, EstadoPedido.RECHAZADO);
        }
        this.estado = EstadoPedido.RECHAZADO;
        this.motivoRechazo = motivo;
        this.actualizadoEn = Instant.now();
    }

    public void despachar() {
        if (estado != EstadoPedido.CONFIRMADO) {
            throw new TransicionEstadoInvalidaException(estado, EstadoPedido.DESPACHADO);
        }
        this.estado = EstadoPedido.DESPACHADO;
        this.actualizadoEn = Instant.now();
    }

    public void cancelar() {
        if (estado != EstadoPedido.PENDIENTE_STOCK && estado != EstadoPedido.CONFIRMADO) {
            throw new TransicionEstadoInvalidaException(estado, EstadoPedido.CANCELADO);
        }
        this.estado = EstadoPedido.CANCELADO;
        this.actualizadoEn = Instant.now();
    }

    public String getId()              { return id; }
    public String getNumeroPedido()    { return numeroPedido; }
    public String getSolicitadoPor()   { return solicitadoPor; }
    public String getHospitalDestino() { return hospitalDestino; }
    public List<LineaDePedido> getLineas() { return lineas; }
    public EstadoPedido getEstado()    { return estado; }
    public String getMotivoRechazo()   { return motivoRechazo; }
    public Instant getCreadoEn()       { return creadoEn; }
    public Instant getActualizadoEn()  { return actualizadoEn; }
}
