package com.grupo10.returns.application.service;

import com.grupo10.returns.application.port.in.RegistrarDevolucionUseCase.RegistrarDevolucionCommand;
import com.grupo10.returns.application.port.in.RegistrarDevolucionUseCase.DevolucionRegistrada;
import com.grupo10.returns.application.port.out.SaveDevolucionPort;
import com.grupo10.returns.domain.model.Devolucion;
import com.grupo10.returns.domain.model.EstadoDevolucion;
import com.grupo10.returns.domain.model.LineaDevolucion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarDevolucionServiceTest {

    @Mock private SaveDevolucionPort saveDevolucionPort;

    @InjectMocks
    private RegistrarDevolucionService service;

    private static final List<LineaDevolucion> LINEAS = List.of(
            new LineaDevolucion("lote-1", 3, "Material deteriorado")
    );

    @Test
    void registrar_should_RetornarDevolucionRegistrada_when_DatosValidos() {
        var devolucionGuardada = Devolucion.registrar("ped-1", "usr-1", LINEAS);
        when(saveDevolucionPort.save(any())).thenReturn(devolucionGuardada);

        DevolucionRegistrada resultado = service.registrar(
                new RegistrarDevolucionCommand("ped-1", "usr-1", LINEAS));

        assertThat(resultado.devolucionId()).isEqualTo(devolucionGuardada.getId());
        assertThat(resultado.numeroDevolucion()).startsWith("DEV-");
    }

    @Test
    void registrar_should_GuardarEnEstadoPendiente_when_DevolucionCreada() {
        when(saveDevolucionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(new RegistrarDevolucionCommand("ped-1", "usr-1", LINEAS));

        var captor = ArgumentCaptor.forClass(Devolucion.class);
        verify(saveDevolucionPort).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoDevolucion.PENDIENTE);
    }

    @Test
    void registrar_should_AsociarPedidoId_when_DevolucionCreada() {
        when(saveDevolucionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(new RegistrarDevolucionCommand("ped-abc", "usr-1", LINEAS));

        var captor = ArgumentCaptor.forClass(Devolucion.class);
        verify(saveDevolucionPort).save(captor.capture());
        assertThat(captor.getValue().getPedidoId()).isEqualTo("ped-abc");
    }

    @Test
    void registrar_should_IncluirTodasLasLineas_when_MultipleLineas() {
        var lineas = List.of(
                new LineaDevolucion("lote-1", 2, "Vencido"),
                new LineaDevolucion("lote-2", 5, "Dañado")
        );
        when(saveDevolucionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(new RegistrarDevolucionCommand("ped-1", "usr-1", lineas));

        var captor = ArgumentCaptor.forClass(Devolucion.class);
        verify(saveDevolucionPort).save(captor.capture());
        assertThat(captor.getValue().getLineas()).hasSize(2);
    }
}
