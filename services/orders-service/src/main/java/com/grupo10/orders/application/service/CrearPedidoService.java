package com.grupo10.orders.application.service;

import com.grupo10.orders.application.port.in.CrearPedidoUseCase;
import com.grupo10.orders.application.port.out.PublishPedidoEventPort;
import com.grupo10.orders.application.port.out.SavePedidoPort;
import com.grupo10.orders.domain.event.PedidoCreadoEvent;
import com.grupo10.orders.domain.model.Pedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearPedidoService implements CrearPedidoUseCase {

    private final SavePedidoPort savePedidoPort;
    private final PublishPedidoEventPort publishPedidoEventPort;

    public CrearPedidoService(SavePedidoPort savePedidoPort,
                               PublishPedidoEventPort publishPedidoEventPort) {
        this.savePedidoPort = savePedidoPort;
        this.publishPedidoEventPort = publishPedidoEventPort;
    }

    @Override
    public PedidoCreado crear(CrearPedidoCommand command) {
        Pedido pedido = Pedido.crear(
                command.solicitadoPor(),
                command.hospitalDestino(),
                command.lineas()
        );
        Pedido saved = savePedidoPort.save(pedido);

        PedidoCreadoEvent event = PedidoCreadoEvent.of(
                saved.getId(),
                saved.getSolicitadoPor(),
                saved.getHospitalDestino(),
                saved.getLineas()
        );
        publishPedidoEventPort.publish(event);

        return new PedidoCreado(saved.getId(), saved.getNumeroPedido());
    }
}
