package com.grupo10.inventory.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo10.inventory.application.port.in.ConsultarStockUseCase;
import com.grupo10.inventory.application.port.in.RegistrarMovimientoUseCase;
import com.grupo10.inventory.application.port.in.RegistrarMovimientoUseCase.MovimientoResult;
import com.grupo10.inventory.domain.exception.LoteNoEncontradoException;
import com.grupo10.inventory.domain.exception.StockInsuficienteException;
import com.grupo10.inventory.domain.model.Lote;
import com.grupo10.inventory.domain.model.TipoMovimiento;
import com.grupo10.inventory.infrastructure.adapter.in.rest.dto.RegistrarMovimientoRequest;
import com.grupo10.inventory.infrastructure.adapter.in.rest.dto.StockResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
@Import({StockControllerMapper.class, GlobalExceptionHandler.class})
class StockControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private RegistrarMovimientoUseCase registrarMovimientoUseCase;
    @MockBean private ConsultarStockUseCase consultarStockUseCase;
    // Evita que Spring intente conectar al issuer-uri de JWT en tests de slice
    @MockBean private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void registrarMovimiento_should_RetornarCreated_when_MovimientoExitoso() throws Exception {
        when(registrarMovimientoUseCase.registrar(any()))
                .thenReturn(new MovimientoResult("mov-123", "lote-1", 70));

        var request = new RegistrarMovimientoRequest("lote-1", TipoMovimiento.SALIDA, 30, "PED-001");

        mockMvc.perform(post("/api/v1/stock/movimientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movimientoId").value("mov-123"))
                .andExpect(jsonPath("$.loteId").value("lote-1"))
                .andExpect(jsonPath("$.cantidadResultante").value(70));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void registrarMovimiento_should_RetornarConflict_when_StockInsuficiente() throws Exception {
        when(registrarMovimientoUseCase.registrar(any()))
                .thenThrow(new StockInsuficienteException("lote-1", 10, 50));

        var request = new RegistrarMovimientoRequest("lote-1", TipoMovimiento.SALIDA, 50, null);

        mockMvc.perform(post("/api/v1/stock/movimientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Stock insuficiente"))
                .andExpect(jsonPath("$.disponible").value(10))
                .andExpect(jsonPath("$.solicitado").value(50));
    }

    @Test
    @WithMockUser(roles = "HOSPITAL_STAFF")
    void registrarMovimiento_should_RetornarForbidden_when_RolNoAutorizado() throws Exception {
        var request = new RegistrarMovimientoRequest("lote-1", TipoMovimiento.SALIDA, 10, null);

        mockMvc.perform(post("/api/v1/stock/movimientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void registrarMovimiento_should_RetornarBadRequest_when_CamposInvalidos() throws Exception {
        // cantidad=0 viola @Min(1)
        var request = new RegistrarMovimientoRequest("lote-1", TipoMovimiento.SALIDA, 0, null);

        mockMvc.perform(post("/api/v1/stock/movimientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void consultarLote_should_RetornarNotFound_when_LoteInexistente() throws Exception {
        when(consultarStockUseCase.consultarLote(anyString()))
                .thenThrow(new LoteNoEncontradoException("lote-xyz"));

        mockMvc.perform(get("/api/v1/stock/lotes/lote-xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void consultarLote_should_RetornarStock_when_LoteExiste() throws Exception {
        var lote = new Lote("lote-1", "prod-1", "L-2024-001",
                LocalDate.of(2025, 12, 31), 80);
        var response = new StockResponse("lote-1", "prod-1", "L-2024-001",
                LocalDate.of(2025, 12, 31), 80, false);

        when(consultarStockUseCase.consultarLote("lote-1")).thenReturn(lote);

        mockMvc.perform(get("/api/v1/stock/lotes/lote-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("lote-1"))
                .andExpect(jsonPath("$.cantidadDisponible").value(80));
    }
}
