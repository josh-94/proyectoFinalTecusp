package com.grupo10.returns.infrastructure.adapter.in.rest;

import com.grupo10.returns.application.port.in.*;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.DevolucionResponse;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.InspeccionarDevolucionRequest;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.RechazarDevolucionRequest;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.RegistrarDevolucionRequest;
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
@RequestMapping("/api/v1/devoluciones")
@Tag(name = "Devoluciones", description = "Registro e inspección de devoluciones parciales de instrumental médico")
@SecurityRequirement(name = "bearerAuth")
public class DevolucionController {

    private final RegistrarDevolucionUseCase registrarDevolucionUseCase;
    private final ConsultarDevolucionUseCase consultarDevolucionUseCase;
    private final InspeccionarDevolucionUseCase inspeccionarDevolucionUseCase;
    private final AprobarDevolucionUseCase aprobarDevolucionUseCase;
    private final RechazarDevolucionUseCase rechazarDevolucionUseCase;
    private final DevolucionControllerMapper mapper;

    public DevolucionController(RegistrarDevolucionUseCase registrarDevolucionUseCase,
                                 ConsultarDevolucionUseCase consultarDevolucionUseCase,
                                 InspeccionarDevolucionUseCase inspeccionarDevolucionUseCase,
                                 AprobarDevolucionUseCase aprobarDevolucionUseCase,
                                 RechazarDevolucionUseCase rechazarDevolucionUseCase,
                                 DevolucionControllerMapper mapper) {
        this.registrarDevolucionUseCase = registrarDevolucionUseCase;
        this.consultarDevolucionUseCase = consultarDevolucionUseCase;
        this.inspeccionarDevolucionUseCase = inspeccionarDevolucionUseCase;
        this.aprobarDevolucionUseCase = aprobarDevolucionUseCase;
        this.rechazarDevolucionUseCase = rechazarDevolucionUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'ADMIN')")
    @Operation(summary = "Registrar solicitud de devolución")
    public ResponseEntity<DevolucionResponse> registrar(
            @Valid @RequestBody RegistrarDevolucionRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var command = new RegistrarDevolucionUseCase.RegistrarDevolucionCommand(
                request.pedidoId(),
                jwt.getSubject(),
                mapper.toLineas(request.lineas())
        );
        var resultado = registrarDevolucionUseCase.registrar(command);
        var devolucion = consultarDevolucionUseCase.consultarPorId(resultado.devolucionId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(resultado.devolucionId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(devolucion));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR', 'AUDITOR', 'WAREHOUSE_OPERATOR')")
    @Operation(summary = "Listar todas las devoluciones")
    public List<DevolucionResponse> listarTodas() {
        return consultarDevolucionUseCase.consultarTodas().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR', 'AUDITOR', 'WAREHOUSE_OPERATOR', 'HOSPITAL_STAFF')")
    @Operation(summary = "Consultar devolución por ID")
    public DevolucionResponse consultarPorId(@PathVariable String id) {
        return mapper.toResponse(consultarDevolucionUseCase.consultarPorId(id));
    }

    @PatchMapping("/{id}/inspeccionar")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    @Operation(summary = "Registrar inspección de la devolución")
    public ResponseEntity<Void> inspeccionar(
            @PathVariable String id,
            @Valid @RequestBody InspeccionarDevolucionRequest request) {
        inspeccionarDevolucionUseCase.inspeccionar(id, request.observaciones());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    @Operation(summary = "Aprobar devolución y liberar stock")
    public ResponseEntity<Void> aprobar(@PathVariable String id) {
        aprobarDevolucionUseCase.aprobar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    @Operation(summary = "Rechazar devolución")
    public ResponseEntity<Void> rechazar(
            @PathVariable String id,
            @Valid @RequestBody RechazarDevolucionRequest request) {
        rechazarDevolucionUseCase.rechazar(id, request.motivo());
        return ResponseEntity.noContent().build();
    }
}
