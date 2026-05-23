package com.grupo10.orders.application.service;

import com.grupo10.orders.application.port.in.CrearPedidoUseCase.CrearPedidoCommand;
import com.grupo10.orders.application.port.in.CrearPedidoUseCase.PedidoCreado;
import com.grupo10.orders.application.port.out.PublishPedidoEventPort;
import com.grupo10.orders.application.port.out.SavePedidoPort;
import com.grupo10.orders.domain.event.PedidoCreadoEvent;
import com.grupo10.orders.domain.model.EstadoPedido;
import com.grupo10.orders.domain.model.LineaDePedido;
import com.grupo10.orders.domain.model.Pedido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearPedidoServiceTest {

    @Mock private SavePedidoPort savePedidoPort;
    @Mock private PublishPedidoEventPort publishPedidoEventPort;

    @InjectMocks
    private CrearPedidoService service;

    private static final List<LineaDePedido> LINEAS = List.of(
            new LineaDePedido("lote-1", 5, "Jeringa 10ml")
    );

    @Test
    void crear_should_RetornarPedidoCreado_when_DatosValidos() {
        var pedidoGuardado = Pedido.crear("usr-1", "Hospital ABC", LINEAS);
        when(savePedidoPort.save(any())).thenReturn(pedidoGuardado);

        PedidoCreado resultado = service.crear(
                new CrearPedidoCommand("usr-1", "Hospital ABC", LINEAS));

        assertThat(resultado.pedidoId()).isEqualTo(pedidoGuardado.getId());
        assertThat(resultado.numeroPedido()).startsWith("PED-");
    }

    @Test
    void crear_should_PublicarEvento_when_PedidoCreado() {
        var pedidoGuardado = Pedido.crear("usr-1", "Hospital ABC", LINEAS);
        when(savePedidoPort.save(any())).thenReturn(pedidoGuardado);

        service.crear(new CrearPedidoCommand("usr-1", "Hospital ABC", LINEAS));

        var captor = ArgumentCaptor.forClass(PedidoCreadoEvent.class);
        verify(publishPedidoEventPort).publish(captor.capture());

        PedidoCreadoEvent evento = captor.getValue();
        assertThat(evento.data().pedidoId()).isEqualTo(pedidoGuardado.getId());
        assertThat(evento.data().items()).hasSize(1);
        assertThat(evento.data().items().get(0).loteId()).isEqualTo("lote-1");
        assertThat(evento.data().items().get(0).cantidad()).isEqualTo(5);
    }

    @Test
    void crear_should_GuardarPedidoEnPendienteStock_when_Creado() {
        when(savePedidoPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.crear(new CrearPedidoCommand("usr-1", "Hospital ABC", LINEAS));

        var captor = ArgumentCaptor.forClass(Pedido.class);
        verify(savePedidoPort).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoPedido.PENDIENTE_STOCK);
    }

    @Test
    void crear_should_IncluirTodasLasLineas_when_MultipleLineas() {
        var lineas = List.of(
                new LineaDePedido("lote-1", 3, "Guante M"),
                new LineaDePedido("lote-2", 10, "Mascarilla N95")
        );
        when(savePedidoPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.crear(new CrearPedidoCommand("usr-1", "Hospital B", lineas));

        var captor = ArgumentCaptor.forClass(PedidoCreadoEvent.class);
        verify(publishPedidoEventPort).publish(captor.capture());
        assertThat(captor.getValue().data().items()).hasSize(2);
    }
}
