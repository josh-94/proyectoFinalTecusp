package com.grupo10.returns.domain.model;

import com.grupo10.returns.domain.exception.TransicionEstadoInvalidaException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Devolucion {

    private final String id;
    private final String numeroDevolucion;
    private final String pedidoId;
    private final String solicitadoPor;
    private final List<LineaDevolucion> lineas;
    private EstadoDevolucion estado;
    private String observaciones;
    private String motivoRechazo;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Devolucion(String id, String numeroDevolucion, String pedidoId, String solicitadoPor,
                      List<LineaDevolucion> lineas, EstadoDevolucion estado,
                      String observaciones, String motivoRechazo,
                      Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.numeroDevolucion = numeroDevolucion;
        this.pedidoId = pedidoId;
        this.solicitadoPor = solicitadoPor;
        this.lineas = Collections.unmodifiableList(lineas);
        this.estado = estado;
        this.observaciones = observaciones;
        this.motivoRechazo = motivoRechazo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public static Devolucion registrar(String pedidoId, String solicitadoPor,
                                       List<LineaDevolucion> lineas) {
        Instant now = Instant.now();
        String id = UUID.randomUUID().toString();
        String numero = "DEV-" + id.substring(0, 8).toUpperCase();
        return new Devolucion(id, numero, pedidoId, solicitadoPor,
                lineas, EstadoDevolucion.PENDIENTE, null, null, now, now);
    }

    public void inspeccionar(String observaciones) {
        if (estado != EstadoDevolucion.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(estado, EstadoDevolucion.INSPECCIONADA);
        }
        this.estado = EstadoDevolucion.INSPECCIONADA;
        this.observaciones = observaciones;
        this.actualizadoEn = Instant.now();
    }

    public void aprobar() {
        if (estado != EstadoDevolucion.INSPECCIONADA) {
            throw new TransicionEstadoInvalidaException(estado, EstadoDevolucion.APROBADA);
        }
        this.estado = EstadoDevolucion.APROBADA;
        this.actualizadoEn = Instant.now();
    }

    public void rechazar(String motivo) {
        if (estado != EstadoDevolucion.INSPECCIONADA) {
            throw new TransicionEstadoInvalidaException(estado, EstadoDevolucion.RECHAZADA);
        }
        this.estado = EstadoDevolucion.RECHAZADA;
        this.motivoRechazo = motivo;
        this.actualizadoEn = Instant.now();
    }

    public String getId()                  { return id; }
    public String getNumeroDevolucion()    { return numeroDevolucion; }
    public String getPedidoId()            { return pedidoId; }
    public String getSolicitadoPor()       { return solicitadoPor; }
    public List<LineaDevolucion> getLineas() { return lineas; }
    public EstadoDevolucion getEstado()    { return estado; }
    public String getObservaciones()       { return observaciones; }
    public String getMotivoRechazo()       { return motivoRechazo; }
    public Instant getCreadoEn()           { return creadoEn; }
    public Instant getActualizadoEn()      { return actualizadoEn; }
}
