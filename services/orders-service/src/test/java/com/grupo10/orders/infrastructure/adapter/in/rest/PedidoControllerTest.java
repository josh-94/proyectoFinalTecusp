package com.grupo10.orders.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo10.orders.application.port.in.*;
import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import com.grupo10.orders.domain.exception.TransicionEstadoInvalidaException;
import com.grupo10.orders.domain.model.EstadoPedido;
import com.grupo10.orders.domain.model.LineaDePedido;
import com.grupo10.orders.domain.model.Pedido;
import com.grupo10.orders.infrastructure.adapter.in.rest.dto.CrearPedidoRequest;
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

@WebMvcTest(PedidoController.class)
@Import({PedidoControllerMapper.class, GlobalExceptionHandler.class})
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CrearPedidoUseCase crearPedidoUseCase;
    @MockBean private ConsultarPedidoUseCase consultarPedidoUseCase;
    @MockBean private DespacharPedidoUseCase despacharPedidoUseCase;
    @MockBean private CancelarPedidoUseCase cancelarPedidoUseCase;
    @MockBean private JwtDecoder jwtDecoder;

    private Pedido pedidoFake() {
        return new Pedido(
                "ped-1", "PED-ABCD1234", "usr-1", "Hospital Central",
                List.of(new LineaDePedido("lote-1", 5, "Jeringa 10ml")),
                EstadoPedido.PENDIENTE_STOCK, null, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser(roles = "HOSPITAL_STAFF")
    void crear_should_Retornar201_when_DatosValidos() throws Exception {
        var pedido = pedidoFake();
        when(crearPedidoUseCase.crear(any()))
                .thenReturn(new CrearPedidoUseCase.PedidoCreado("ped-1", "PED-ABCD1234"));
        when(consultarPedidoUseCase.consultarPorId("ped-1")).thenReturn(pedido);

        var request = new CrearPedidoRequest("Hospital Central",
                List.of(new CrearPedidoRequest.LineaDePedidoRequest("lote-1", 5, "Jeringa 10ml")));

        mockMvc.perform(post("/api/v1/pedidos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroPedido").value("PED-ABCD1234"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE_STOCK"));
    }

    @Test
    @WithMockUser(roles = "HOSPITAL_STAFF")
    void crear_should_Retornar400_when_SinLineas() throws Exception {
        var request = new CrearPedidoRequest("Hospital Central", List.of());

        mockMvc.perform(post("/api/v1/pedidos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void crear_should_Retornar403_when_RolNoAutorizado() throws Exception {
        var request = new CrearPedidoRequest("Hospital Central",
                List.of(new CrearPedidoRequest.LineaDePedidoRequest("lote-1", 5, "Item")));

        mockMvc.perform(post("/api/v1/pedidos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void consultarPorId_should_Retornar200_when_Existe() throws Exception {
        when(consultarPedidoUseCase.consultarPorId("ped-1")).thenReturn(pedidoFake());

        mockMvc.perform(get("/api/v1/pedidos/ped-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ped-1"))
                .andExpect(jsonPath("$.hospitalDestino").value("Hospital Central"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void consultarPorId_should_Retornar404_when_NoExiste() throws Exception {
        when(consultarPedidoUseCase.consultarPorId("no-existe"))
                .thenThrow(new PedidoNoEncontradoException("no-existe"));

        mockMvc.perform(get("/api/v1/pedidos/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Pedido no encontrado"));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void despachar_should_Retornar204_when_Exito() throws Exception {
        doNothing().when(despacharPedidoUseCase).despachar("ped-1");

        mockMvc.perform(patch("/api/v1/pedidos/ped-1/despachar").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void despachar_should_Retornar409_when_EstadoInvalido() throws Exception {
        doThrow(new TransicionEstadoInvalidaException(EstadoPedido.PENDIENTE_STOCK, EstadoPedido.DESPACHADO))
                .when(despacharPedidoUseCase).despachar("ped-1");

        mockMvc.perform(patch("/api/v1/pedidos/ped-1/despachar").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Transición de estado inválida"));
    }
}
