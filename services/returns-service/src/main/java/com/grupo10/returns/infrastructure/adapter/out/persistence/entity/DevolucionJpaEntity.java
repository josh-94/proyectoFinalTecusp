package com.grupo10.returns.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devoluciones")
public class DevolucionJpaEntity {

    @Id
    private String id;

    @Column(name = "numero_devolucion", nullable = false, unique = true)
    private String numeroDevolucion;

    @Column(name = "pedido_id", nullable = false)
    private String pedidoId;

    @Column(name = "solicitado_por", nullable = false)
    private String solicitadoPor;

    @Column(nullable = false)
    private String estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LineaDevolucionJpaEntity> lineas = new ArrayList<>();

    public DevolucionJpaEntity() {}

    public String getId()                      { return id; }
    public void setId(String id)               { this.id = id; }
    public String getNumeroDevolucion()        { return numeroDevolucion; }
    public void setNumeroDevolucion(String n)  { this.numeroDevolucion = n; }
    public String getPedidoId()                { return pedidoId; }
    public void setPedidoId(String p)          { this.pedidoId = p; }
    public String getSolicitadoPor()           { return solicitadoPor; }
    public void setSolicitadoPor(String s)     { this.solicitadoPor = s; }
    public String getEstado()                  { return estado; }
    public void setEstado(String e)            { this.estado = e; }
    public String getObservaciones()           { return observaciones; }
    public void setObservaciones(String o)     { this.observaciones = o; }
    public String getMotivoRechazo()           { return motivoRechazo; }
    public void setMotivoRechazo(String m)     { this.motivoRechazo = m; }
    public Instant getCreadoEn()               { return creadoEn; }
    public void setCreadoEn(Instant c)         { this.creadoEn = c; }
    public Instant getActualizadoEn()          { return actualizadoEn; }
    public void setActualizadoEn(Instant a)    { this.actualizadoEn = a; }
    public List<LineaDevolucionJpaEntity> getLineas()         { return lineas; }
    public void setLineas(List<LineaDevolucionJpaEntity> l)   { this.lineas = l; }
}
