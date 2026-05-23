package com.grupo10.identity.application.service;

import com.grupo10.identity.application.port.in.AutenticarUseCase.AutenticarCommand;
import com.grupo10.identity.application.port.in.AutenticarUseCase.TokenPair;
import com.grupo10.identity.application.port.out.GenerarJwtPort;
import com.grupo10.identity.application.port.out.HashPasswordPort;
import com.grupo10.identity.application.port.out.LoadUsuarioPort;
import com.grupo10.identity.application.port.out.SaveRefreshTokenPort;
import com.grupo10.identity.domain.exception.CredencialesInvalidasException;
import com.grupo10.identity.domain.model.RefreshToken;
import com.grupo10.identity.domain.model.Rol;
import com.grupo10.identity.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticarServiceTest {

    @Mock private LoadUsuarioPort loadUsuarioPort;
    @Mock private HashPasswordPort hashPasswordPort;
    @Mock private GenerarJwtPort generarJwtPort;
    @Mock private SaveRefreshTokenPort saveRefreshTokenPort;

    @InjectMocks
    private AutenticarService service;

    private static final String USERNAME = "operador1";
    private static final String PASSWORD = "pass123";
    private static final String HASH     = "hashed-pass";

    private Usuario usuarioActivo;

    @BeforeEach
    void setUp() {
        usuarioActivo = new Usuario("usr-1", USERNAME, "op@hospital.com", "Operador Uno",
                HASH, Set.of(Rol.WAREHOUSE_OPERATOR), true, Instant.now());
    }

    @Test
    void autenticar_should_RetornarTokens_when_CredencialesValidas() {
        when(loadUsuarioPort.findByUsername(USERNAME)).thenReturn(Optional.of(usuarioActivo));
        when(hashPasswordPort.verify(PASSWORD, HASH)).thenReturn(true);
        when(generarJwtPort.generarAccessToken(usuarioActivo)).thenReturn("jwt.signed.token");

        TokenPair result = service.autenticar(new AutenticarCommand(USERNAME, PASSWORD));

        assertThat(result.accessToken()).isEqualTo("jwt.signed.token");
        assertThat(result.refreshToken()).isNotBlank();

        var captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(saveRefreshTokenPort).save(captor.capture());
        assertThat(captor.getValue().getUsuarioId()).isEqualTo("usr-1");
        assertThat(captor.getValue().isRevocado()).isFalse();
    }

    @Test
    void autenticar_should_LanzarExcepcion_when_UsuarioNoExiste() {
        when(loadUsuarioPort.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.autenticar(new AutenticarCommand(USERNAME, PASSWORD)))
                .isInstanceOf(CredencialesInvalidasException.class);

        verifyNoInteractions(hashPasswordPort, generarJwtPort, saveRefreshTokenPort);
    }

    @Test
    void autenticar_should_LanzarExcepcion_when_PasswordIncorrecto() {
        when(loadUsuarioPort.findByUsername(USERNAME)).thenReturn(Optional.of(usuarioActivo));
        when(hashPasswordPort.verify(PASSWORD, HASH)).thenReturn(false);

        assertThatThrownBy(() -> service.autenticar(new AutenticarCommand(USERNAME, PASSWORD)))
                .isInstanceOf(CredencialesInvalidasException.class);

        verifyNoInteractions(generarJwtPort, saveRefreshTokenPort);
    }

    @Test
    void autenticar_should_LanzarExcepcion_when_UsuarioInactivo() {
        var inactivo = new Usuario("usr-2", USERNAME, "op@hospital.com", "Inactivo",
                HASH, Set.of(Rol.WAREHOUSE_OPERATOR), false, Instant.now());
        when(loadUsuarioPort.findByUsername(USERNAME)).thenReturn(Optional.of(inactivo));

        assertThatThrownBy(() -> service.autenticar(new AutenticarCommand(USERNAME, PASSWORD)))
                .isInstanceOf(CredencialesInvalidasException.class);

        verifyNoInteractions(hashPasswordPort, generarJwtPort, saveRefreshTokenPort);
    }

    @Test
    void autenticar_should_GenerarRefreshTokenConHash_when_LoginExitoso() {
        when(loadUsuarioPort.findByUsername(USERNAME)).thenReturn(Optional.of(usuarioActivo));
        when(hashPasswordPort.verify(PASSWORD, HASH)).thenReturn(true);
        when(generarJwtPort.generarAccessToken(any())).thenReturn("token");

        TokenPair result = service.autenticar(new AutenticarCommand(USERNAME, PASSWORD));

        // El refresh token retornado (raw) no debe ser igual al hash guardado
        var captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(saveRefreshTokenPort).save(captor.capture());
        String savedHash = captor.getValue().getTokenHash();
        assertThat(savedHash).isNotEqualTo(result.refreshToken());
        // Verificamos que el hash guardado es efectivamente el SHA-256 del raw token
        assertThat(savedHash).isEqualTo(AutenticarService.sha256(result.refreshToken()));
    }
}
