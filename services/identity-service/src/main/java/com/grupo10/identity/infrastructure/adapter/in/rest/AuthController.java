package com.grupo10.identity.infrastructure.adapter.in.rest;

import com.grupo10.identity.application.port.in.AutenticarUseCase;
import com.grupo10.identity.application.port.in.RefrescarTokenUseCase;
import com.grupo10.identity.application.port.in.RegistrarUsuarioUseCase;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.RefreshRequest;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.RegistrarUsuarioRequest;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.TokenResponse;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Login, refresh de tokens y creación de usuarios")
public class AuthController {

    private final AutenticarUseCase autenticarUseCase;
    private final RefrescarTokenUseCase refrescarTokenUseCase;
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    public AuthController(AutenticarUseCase autenticarUseCase,
                          RefrescarTokenUseCase refrescarTokenUseCase,
                          RegistrarUsuarioUseCase registrarUsuarioUseCase) {
        this.autenticarUseCase = autenticarUseCase;
        this.refrescarTokenUseCase = refrescarTokenUseCase;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticarse y obtener access token + refresh token")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        var pair = autenticarUseCase.autenticar(
                new AutenticarUseCase.AutenticarCommand(request.username(), request.password()));
        return TokenResponse.login(pair.accessToken(), pair.refreshToken());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Obtener un nuevo access token usando el refresh token")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        var result = refrescarTokenUseCase.refrescar(request.refreshToken());
        return TokenResponse.refresh(result.accessToken());
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo usuario en el sistema (solo ADMIN)")
    public UsuarioResponse registrarUsuario(@Valid @RequestBody RegistrarUsuarioRequest request) {
        var command = new RegistrarUsuarioUseCase.RegistrarUsuarioCommand(
                request.username(), request.email(), request.nombre(),
                request.password(), request.roles());
        var created = registrarUsuarioUseCase.registrar(command);
        return new UsuarioResponse(
                created.id(), created.username(), created.email(),
                created.nombre(), created.roles(), true, created.creadoEn());
    }
}
