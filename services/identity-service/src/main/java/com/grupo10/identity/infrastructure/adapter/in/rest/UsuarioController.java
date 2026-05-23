package com.grupo10.identity.infrastructure.adapter.in.rest;

import com.grupo10.identity.application.port.in.ConsultarUsuarioUseCase;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/usuarios")
@Tag(name = "Usuarios", description = "Consulta de usuarios del sistema")
public class UsuarioController {

    private final ConsultarUsuarioUseCase consultarUsuarioUseCase;

    public UsuarioController(ConsultarUsuarioUseCase consultarUsuarioUseCase) {
        this.consultarUsuarioUseCase = consultarUsuarioUseCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los usuarios (solo ADMIN)")
    public List<UsuarioResponse> listar() {
        return consultarUsuarioUseCase.listarTodos().stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener un usuario por ID (solo ADMIN)")
    public UsuarioResponse consultarPorId(@PathVariable String id) {
        return UsuarioResponse.from(consultarUsuarioUseCase.consultarPorId(id));
    }
}
