package com.grupo10.inventory.application.service;

import com.grupo10.inventory.application.port.in.RegistrarMovimientoUseCase.MovimientoResult;
import com.grupo10.inventory.application.port.in.RegistrarMovimientoUseCase.RegistrarMovimientoCommand;
import com.grupo10.inventory.application.port.out.LoadLotePort;
import com.grupo10.inventory.application.port.out.PublishDomainEventPort;
import com.grupo10.inventory.application.port.out.SaveMovimientoPort;
import com.grupo10.inventory.domain.event.StockMovimientoRegistradoEvent;
import com.grupo10.inventory.domain.exception.LoteNoEncontradoException;
import com.grupo10.inventory.domain.exception.StockInsuficienteException;
import com.grupo10.inventory.domain.model.Lote;
import com.grupo10.inventory.domain.model.TipoMovimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarMovimientoServiceTest {

    @Mock private LoadLotePort loadLotePort;
    @Mock private SaveMovimientoPort saveMovimientoPort;
    @Mock private PublishDomainEventPort publishEventPort;

    @InjectMocks
    private RegistrarMovimientoService service;

    private Lote loteConStock;

    @BeforeEach
    void setUp() {
        loteConStock = new Lote("lote-1", "prod-1", "L-2024-001",
                LocalDate.now().plusMonths(6), 100);
    }

    @Test
    void registrar_should_LanzarExcepcion_when_LoteNoExiste() {
        when(loadLotePort.findLoteById("lote-inexistente")).thenReturn(Optional.empty());

        var command = new RegistrarMovimientoCommand(
                "lote-inexistente", TipoMovimiento.SALIDA, 10, null, "user-1");

        assertThatThrownBy(() -> service.registrar(command))
                .isInstanceOf(LoteNoEncontradoException.class)
                .hasMessageContaining("lote-inexistente");

        verifyNoInteractions(saveMovimientoPort, publishEventPort);
    }

    @Test
    void registrar_should_LanzarExcepcion_when_StockInsuficiente() {
        when(loadLotePort.findLoteById("lote-1")).thenReturn(Optional.of(loteConStock));

        var command = new RegistrarMovimientoCommand(
                "lote-1", TipoMovimiento.SALIDA, 150, null, "user-1");

        assertThatThrownBy(() -> service.registrar(command))
                .isInstanceOf(StockInsuficienteException.class);

        verifyNoInteractions(saveMovimientoPort, publishEventPort);
    }

    @Test
    void registrar_should_PublicarEvento_when_SalidaExitosa() {
        when(loadLotePort.findLoteById("lote-1")).thenReturn(Optional.of(loteConStock));
        when(saveMovimientoPort.saveMovimiento(any(), any(), anyInt(), any(), any()))
                .thenReturn("mov-uuid-123");

        var command = new RegistrarMovimientoCommand(
                "lote-1", TipoMovimiento.SALIDA, 30, "PED-001", "user-1");

        MovimientoResult result = service.registrar(command);

        assertThat(result.movimientoId()).isEqualTo("mov-uuid-123");
        assertThat(result.cantidadResultante()).isEqualTo(70);

        var eventCaptor = ArgumentCaptor.forClass(StockMovimientoRegistradoEvent.class);
        verify(publishEventPort).publish(eventCaptor.capture());
        var evento = eventCaptor.getValue();
        assertThat(evento.loteId()).isEqualTo("lote-1");
        assertThat(evento.tipo()).isEqualTo(TipoMovimiento.SALIDA);
        assertThat(evento.cantidad()).isEqualTo(30);
        assertThat(evento.referenciaExterna()).isEqualTo("PED-001");
    }

    @Test
    void registrar_should_IncrementarStock_when_TipoEntrada() {
        when(loadLotePort.findLoteById("lote-1")).thenReturn(Optional.of(loteConStock));
        when(saveMovimientoPort.saveMovimiento(any(), any(), anyInt(), any(), any()))
                .thenReturn("mov-uuid-456");

        var command = new RegistrarMovimientoCommand(
                "lote-1", TipoMovimiento.ENTRADA, 50, null, "user-1");

        MovimientoResult result = service.registrar(command);

        assertThat(result.cantidadResultante()).isEqualTo(150);
        verify(publishEventPort).publish(any());
    }

    @Test
    void registrar_should_DescontarStock_when_TipoReserva() {
        when(loadLotePort.findLoteById("lote-1")).thenReturn(Optional.of(loteConStock));
        when(saveMovimientoPort.saveMovimiento(any(), any(), anyInt(), any(), any()))
                .thenReturn("mov-uuid-789");

        var command = new RegistrarMovimientoCommand(
                "lote-1", TipoMovimiento.RESERVA, 20, "PED-002", "orders-service");

        MovimientoResult result = service.registrar(command);

        assertThat(result.cantidadResultante()).isEqualTo(80);
    }
}
