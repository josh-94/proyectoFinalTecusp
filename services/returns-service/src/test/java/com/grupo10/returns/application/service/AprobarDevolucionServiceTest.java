package com.grupo10.returns.application.service;

import com.grupo10.returns.application.port.out.LoadDevolucionPort;
import com.grupo10.returns.application.port.out.PublishDevolucionEventPort;
import com.grupo10.returns.application.port.out.SaveDevolucionPort;
import com.grupo10.returns.domain.event.DevolucionAprobadaEvent;
import com.grupo10.returns.domain.exception.DevolucionNoEncontradaException;
import com.grupo10.returns.domain.exception.TransicionEstadoInvalidaException;
import com.grupo10.returns.domain.model.Devolucion;
import com.grupo10.returns.domain.model.EstadoDevolucion;
import com.grupo10.returns.domain.model.LineaDevolucion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AprobarDevolucionServiceTest {

    @Mock private LoadDevolucionPort loadDevolucionPort;
    @Mock private SaveDevolucionPort saveDevolucionPort;
    @Mock private PublishDevolucionEventPort publishDevolucionEventPort;

    @InjectMocks
    private AprobarDevolucionService service;

    private Devolucion devolucionInspeccionada;

    @BeforeEach
    void setUp() {
        var lineas = List.of(new LineaDevolucion("lote-1", 3, "Deteriorado"));
        devolucionInspeccionada = new Devolucion(
                "dev-1", "DEV-ABCD1234", "ped-1", "usr-1",
                lineas, EstadoDevolucion.INSPECCIONADA,
                "Inspección OK", null, Instant.now(), Instant.now()
        );
    }

    @Test
    void aprobar_should_CambiarEstadoAAprobada_when_DevolucionInspeccionada() {
        when(loadDevolucionPort.findById("dev-1")).thenReturn(Optional.of(devolucionInspeccionada));
        when(saveDevolucionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.aprobar("dev-1");

        var captor = ArgumentCaptor.forClass(Devolucion.class);
        verify(saveDevolucionPort).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoDevolucion.APROBADA);
    }

    @Test
    void aprobar_should_PublicarEvento_when_DevolucionAprobada() {
        when(loadDevolucionPort.findById("dev-1")).thenReturn(Optional.of(devolucionInspeccionada));
        when(saveDevolucionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.aprobar("dev-1");

        var captor = ArgumentCaptor.forClass(DevolucionAprobadaEvent.class);
        verify(publishDevolucionEventPort).publish(captor.capture());
        assertThat(captor.getValue().data().devolucionId()).isEqualTo("dev-1");
        assertThat(captor.getValue().data().pedidoId()).isEqualTo("ped-1");
        assertThat(captor.getValue().data().lineas()).hasSize(1);
        assertThat(captor.getValue().data().lineas().get(0).loteId()).isEqualTo("lote-1");
    }

    @Test
    void aprobar_should_LanzarExcepcion_when_DevolucionNoExiste() {
        when(loadDevolucionPort.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.aprobar("no-existe"))
                .isInstanceOf(DevolucionNoEncontradaException.class);

        verifyNoInteractions(saveDevolucionPort, publishDevolucionEventPort);
    }

    @Test
    void aprobar_should_LanzarExcepcion_when_EstadoNoBloqueaTransicion() {
        var devPendiente = new Devolucion(
                "dev-2", "DEV-PEND1234", "ped-1", "usr-1",
                List.of(new LineaDevolucion("lote-1", 2, "Test")),
                EstadoDevolucion.PENDIENTE, null, null, Instant.now(), Instant.now()
        );
        when(loadDevolucionPort.findById("dev-2")).thenReturn(Optional.of(devPendiente));

        assertThatThrownBy(() -> service.aprobar("dev-2"))
                .isInstanceOf(TransicionEstadoInvalidaException.class);

        verifyNoInteractions(publishDevolucionEventPort);
    }
}
