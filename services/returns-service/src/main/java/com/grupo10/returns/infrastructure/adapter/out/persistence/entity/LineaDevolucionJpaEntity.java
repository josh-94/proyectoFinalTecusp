package com.grupo10.returns.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lineas_devolucion")
public class LineaDevolucionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private DevolucionJpaEntity devolucion;

    @Column(name = "lote_id", nullable = false)
    private String loteId;

    @Column(name = "cantidad_devuelta", nullable = false)
    private int cantidadDevuelta;

    @Column(name = "motivo_devolucion", nullable = false)
    private String motivoDevolucion;

    public LineaDevolucionJpaEntity() {}

    public Long getId()                               { return id; }
    public DevolucionJpaEntity getDevolucion()        { return devolucion; }
    public void setDevolucion(DevolucionJpaEntity d)  { this.devolucion = d; }
    public String getLoteId()                         { return loteId; }
    public void setLoteId(String l)                   { this.loteId = l; }
    public int getCantidadDevuelta()                  { return cantidadDevuelta; }
    public void setCantidadDevuelta(int c)            { this.cantidadDevuelta = c; }
    public String getMotivoDevolucion()               { return motivoDevolucion; }
    public void setMotivoDevolucion(String m)         { this.motivoDevolucion = m; }
}
