package com.grupo10.returns.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo10.returns.application.port.in.*;
import com.grupo10.returns.domain.exception.DevolucionNoEncontradaException;
import com.grupo10.returns.domain.exception.TransicionEstadoInvalidaException;
import com.grupo10.returns.domain.model.Devolucion;
import com.grupo10.returns.domain.model.EstadoDevolucion;
import com.grupo10.returns.domain.model.LineaDevolucion;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.InspeccionarDevolucionRequest;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.RegistrarDevolucionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DevolucionController.class)
@Import({DevolucionControllerMapper.class, GlobalExceptionHandler.class})
class DevolucionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private RegistrarDevolucionUseCase registrarDevolucionUseCase;
    @MockBean private ConsultarDevolucionUseCase consultarDevolucionUseCase;
    @MockBean private InspeccionarDevolucionUseCase inspeccionarDevolucionUseCase;
    @MockBean private AprobarDevolucionUseCase aprobarDevolucionUseCase;
    @MockBean private RechazarDevolucionUseCase rechazarDevolucionUseCase;
    @MockBean private JwtDecoder jwtDecoder;

    private Devolucion devolucionFake() {
        return new Devolucion(
                "dev-1", "DEV-ABCD1234", "ped-1", "usr-1",
                List.of(new LineaDevolucion("lote-1", 3, "Deteriorado")),
                EstadoDevolucion.PENDIENTE, null, null, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser(roles = "HOSPITAL_STAFF")
    void registrar_should_Retornar201_when_DatosValidos() throws Exception {
        var dev = devolucionFake();
        when(registrarDevolucionUseCase.registrar(any()))
                .thenReturn(new RegistrarDevolucionUseCase.DevolucionRegistrada("dev-1", "DEV-ABCD1234"));
        when(consultarDevolucionUseCase.consultarPorId("dev-1")).thenReturn(dev);

        var request = new RegistrarDevolucionRequest("ped-1",
                List.of(new RegistrarDevolucionRequest.LineaDevolucionRequest("lote-1", 3, "Deteriorado")));

        mockMvc.perform(post("/api/v1/devoluciones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroDevolucion").value("DEV-ABCD1234"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "HOSPITAL_STAFF")
    void registrar_should_Retornar400_when_SinLineas() throws Exception {
        var request = new RegistrarDevolucionRequest("ped-1", List.of());

        mockMvc.perform(post("/api/v1/devoluciones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void consultarPorId_should_Retornar200_when_Existe() throws Exception {
        when(consultarDevolucionUseCase.consultarPorId("dev-1")).thenReturn(devolucionFake());

        mockMvc.perform(get("/api/v1/devoluciones/dev-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("dev-1"))
                .andExpect(jsonPath("$.pedidoId").value("ped-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void consultarPorId_should_Retornar404_when_NoExiste() throws Exception {
        when(consultarDevolucionUseCase.consultarPorId("no-existe"))
                .thenThrow(new DevolucionNoEncontradaException("no-existe"));

        mockMvc.perform(get("/api/v1/devoluciones/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Devolución no encontrada"));
    }

    @Test
    @WithMockUser(roles = "INSPECTOR")
    void inspeccionar_should_Retornar204_when_Exito() throws Exception {
        doNothing().when(inspeccionarDevolucionUseCase).inspeccionar(any(), any());

        mockMvc.perform(patch("/api/v1/devoluciones/dev-1/inspeccionar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new InspeccionarDevolucionRequest("Todo en buen estado"))))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "INSPECTOR")
    void aprobar_should_Retornar409_when_EstadoInvalido() throws Exception {
        doThrow(new TransicionEstadoInvalidaException(EstadoDevolucion.PENDIENTE, EstadoDevolucion.APROBADA))
                .when(aprobarDevolucionUseCase).aprobar("dev-1");

        mockMvc.perform(patch("/api/v1/devoluciones/dev-1/aprobar").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Transición de estado inválida"));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void inspeccionar_should_Retornar403_when_RolNoAutorizado() throws Exception {
        mockMvc.perform(patch("/api/v1/devoluciones/dev-1/inspeccionar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new InspeccionarDevolucionRequest("Observación"))))
                .andExpect(status().isForbidden());
    }
}
