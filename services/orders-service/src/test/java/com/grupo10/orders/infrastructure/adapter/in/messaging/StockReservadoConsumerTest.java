package com.grupo10.orders.infrastructure.adapter.in.messaging;

import com.grupo10.orders.application.port.in.ConfirmarPedidoUseCase;
import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockReservadoConsumerTest {

    @Mock private ConfirmarPedidoUseCase confirmarPedidoUseCase;

    @InjectMocks
    private StockReservadoConsumer consumer;

    @Test
    void onStockReservado_should_ConfirmarPedido_when_PedidoIdPresente() {
        var payload = buildPayload("ped-123");

        consumer.onStockReservado(payload);

        verify(confirmarPedidoUseCase).confirmar("ped-123");
    }

    @Test
    void onStockReservado_should_Ignorar_when_SinPedidoId() {
        var payload = Map.<String, Object>of("data", Map.of());

        consumer.onStockReservado(payload);

        verifyNoInteractions(confirmarPedidoUseCase);
    }

    @Test
    void onStockReservado_should_Ignorar_when_PedidoNoEncontrado() {
        var payload = buildPayload("no-existe");
        doThrow(new PedidoNoEncontradoException("no-existe"))
                .when(confirmarPedidoUseCase).confirmar("no-existe");

        consumer.onStockReservado(payload);

        verify(confirmarPedidoUseCase).confirmar("no-existe");
    }

    @Test
    void onStockReservado_should_Ignorar_when_DataEsNull() {
        var payload = new HashMap<String, Object>();
        payload.put("data", null);

        consumer.onStockReservado(payload);

        verifyNoInteractions(confirmarPedidoUseCase);
    }

    private Map<String, Object> buildPayload(String pedidoId) {
        return Map.of(
                "eventType", "inventory.stock.reservado",
                "producer", "inventory-service",
                "version", 1,
                "data", Map.of("pedidoId", pedidoId)
        );
    }
}
