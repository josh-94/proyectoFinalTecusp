package com.grupo10.inventory.infrastructure.adapter.in.rest;

import com.grupo10.inventory.application.port.in.ConsultarStockUseCase;
import com.grupo10.inventory.application.port.in.RegistrarMovimientoUseCase;
import com.grupo10.inventory.infrastructure.adapter.in.rest.dto.MovimientoResponse;
import com.grupo10.inventory.infrastructure.adapter.in.rest.dto.RegistrarMovimientoRequest;
import com.grupo10.inventory.infrastructure.adapter.in.rest.dto.StockResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@Tag(name = "Stock", description = "Consulta y movimientos de stock de inventario médico")
public class StockController {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;
    private final ConsultarStockUseCase consultarStockUseCase;
    private final StockControllerMapper mapper;

    public StockController(RegistrarMovimientoUseCase registrarMovimientoUseCase,
                           ConsultarStockUseCase consultarStockUseCase,
                           StockControllerMapper mapper) {
        this.registrarMovimientoUseCase = registrarMovimientoUseCase;
        this.consultarStockUseCase = consultarStockUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/movimientos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('WAREHOUSE_OPERATOR') or hasRole('ADMIN')")
    @Operation(summary = "Registra un movimiento de stock (ENTRADA, SALIDA, RESERVA o LIBERACION)")
    public MovimientoResponse registrarMovimiento(
            @Valid @RequestBody RegistrarMovimientoRequest request,
            Principal principal) {

        var command = new RegistrarMovimientoUseCase.RegistrarMovimientoCommand(
                request.loteId(),
                request.tipo(),
                request.cantidad(),
                request.referenciaExterna(),
                principal.getName()
        );
        var result = registrarMovimientoUseCase.registrar(command);
        return new MovimientoResponse(result.movimientoId(), result.loteId(), result.cantidadResultante());
    }

    @GetMapping("/lotes")
    @PreAuthorize("hasAnyRole('WAREHOUSE_OPERATOR', 'ADMIN', 'AUDITOR', 'HOSPITAL_STAFF', 'INSPECTOR')")
    @Operation(summary = "Lista todos los lotes de inventario")
    public List<StockResponse> listarLotes() {
        return consultarStockUseCase.listarTodos()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/lotes/{loteId}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_OPERATOR', 'ADMIN', 'AUDITOR', 'HOSPITAL_STAFF')")
    @Operation(summary = "Consulta el stock de un lote específico")
    public StockResponse consultarLote(@PathVariable String loteId) {
        return mapper.toResponse(consultarStockUseCase.consultarLote(loteId));
    }

    @GetMapping("/productos/{productoId}/lotes")
    @PreAuthorize("hasAnyRole('WAREHOUSE_OPERATOR', 'ADMIN', 'AUDITOR', 'HOSPITAL_STAFF')")
    @Operation(summary = "Lista todos los lotes vigentes de un producto")
    public List<StockResponse> consultarPorProducto(@PathVariable String productoId) {
        return consultarStockUseCase.consultarPorProducto(productoId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
