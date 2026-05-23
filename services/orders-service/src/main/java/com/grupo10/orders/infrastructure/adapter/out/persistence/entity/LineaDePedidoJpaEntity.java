package com.grupo10.orders.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lineas_pedido")
public class LineaDePedidoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoJpaEntity pedido;

    @Column(name = "lote_id", nullable = false)
    private String loteId;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private String descripcion;

    public LineaDePedidoJpaEntity() {}

    public Long getId()                        { return id; }
    public PedidoJpaEntity getPedido()         { return pedido; }
    public void setPedido(PedidoJpaEntity p)   { this.pedido = p; }
    public String getLoteId()                  { return loteId; }
    public void setLoteId(String l)            { this.loteId = l; }
    public int getCantidad()                   { return cantidad; }
    public void setCantidad(int c)             { this.cantidad = c; }
    public String getDescripcion()             { return descripcion; }
    public void setDescripcion(String d)       { this.descripcion = d; }
}
