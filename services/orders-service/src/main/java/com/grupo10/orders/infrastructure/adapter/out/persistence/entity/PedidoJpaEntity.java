package com.grupo10.orders.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class PedidoJpaEntity {

    @Id
    private String id;

    @Column(name = "numero_pedido", nullable = false, unique = true)
    private String numeroPedido;

    @Column(name = "solicitado_por", nullable = false)
    private String solicitadoPor;

    @Column(name = "hospital_destino", nullable = false)
    private String hospitalDestino;

    @Column(nullable = false)
    private String estado;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LineaDePedidoJpaEntity> lineas = new ArrayList<>();

    public PedidoJpaEntity() {}

    public String getId()              { return id; }
    public void setId(String id)       { this.id = id; }

    public String getNumeroPedido()    { return numeroPedido; }
    public void setNumeroPedido(String n) { this.numeroPedido = n; }

    public String getSolicitadoPor()   { return solicitadoPor; }
    public void setSolicitadoPor(String s) { this.solicitadoPor = s; }

    public String getHospitalDestino() { return hospitalDestino; }
    public void setHospitalDestino(String h) { this.hospitalDestino = h; }

    public String getEstado()          { return estado; }
    public void setEstado(String e)    { this.estado = e; }

    public String getMotivoRechazo()   { return motivoRechazo; }
    public void setMotivoRechazo(String m) { this.motivoRechazo = m; }

    public Instant getCreadoEn()       { return creadoEn; }
    public void setCreadoEn(Instant c) { this.creadoEn = c; }

    public Instant getActualizadoEn()  { return actualizadoEn; }
    public void setActualizadoEn(Instant a) { this.actualizadoEn = a; }

    public List<LineaDePedidoJpaEntity> getLineas() { return lineas; }
    public void setLineas(List<LineaDePedidoJpaEntity> lineas) { this.lineas = lineas; }
}
