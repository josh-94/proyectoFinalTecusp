package com.grupo10.orders.infrastructure.adapter.in.rest;

import com.grupo10.orders.application.port.in.CancelarPedidoUseCase;
import com.grupo10.orders.application.port.in.ConsultarPedidoUseCase;
import com.grupo10.orders.application.port.in.CrearPedidoUseCase;
import com.grupo10.orders.application.port.in.DespacharPedidoUseCase;
import com.grupo10.orders.infrastructure.adapter.in.rest.dto.CrearPedidoRequest;
import com.grupo10.orders.infrastructure.adapter.in.rest.dto.PedidoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Ciclo de vida de pedidos de material médico")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final ConsultarPedidoUseCase consultarPedidoUseCase;
    private final DespacharPedidoUseCase despacharPedidoUseCase;
    private final CancelarPedidoUseCase cancelarPedidoUseCase;
    private final PedidoControllerMapper mapper;

    public PedidoController(CrearPedidoUseCase crearPedidoUseCase,
                             ConsultarPedidoUseCase consultarPedidoUseCase,
                             DespacharPedidoUseCase despacharPedidoUseCase,
                             CancelarPedidoUseCase cancelarPedidoUseCase,
                             PedidoControllerMapper mapper) {
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.consultarPedidoUseCase = consultarPedidoUseCase;
        this.despacharPedidoUseCase = despacharPedidoUseCase;
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'ADMIN')")
    @Operation(summary = "Crear pedido de material médico")
    public ResponseEntity<PedidoResponse> crear(
            @Valid @RequestBody CrearPedidoRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var command = new CrearPedidoUseCase.CrearPedidoCommand(
                jwt.getSubject(),
                request.hospitalDestino(),
                mapper.toLineas(request.lineas())
        );
        var resultado = crearPedidoUseCase.crear(command);
        var pedido = consultarPedidoUseCase.consultarPorId(resultado.pedidoId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(resultado.pedidoId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(pedido));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_OPERATOR', 'INSPECTOR', 'AUDITOR')")
    @Operation(summary = "Listar todos los pedidos")
    public List<PedidoResponse> listarTodos() {
        return consultarPedidoUseCase.consultarTodos().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_OPERATOR', 'HOSPITAL_STAFF', 'INSPECTOR', 'AUDITOR')")
    @Operation(summary = "Consultar pedido por ID")
    public PedidoResponse consultarPorId(@PathVariable String id) {
        return mapper.toResponse(consultarPedidoUseCase.consultarPorId(id));
    }

    @PatchMapping("/{id}/despachar")
    @PreAuthorize("hasAnyRole('WAREHOUSE_OPERATOR', 'ADMIN')")
    @Operation(summary = "Marcar pedido como despachado")
    public ResponseEntity<Void> despachar(@PathVariable String id) {
        despacharPedidoUseCase.despachar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'ADMIN')")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<Void> cancelar(@PathVariable String id) {
        cancelarPedidoUseCase.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
