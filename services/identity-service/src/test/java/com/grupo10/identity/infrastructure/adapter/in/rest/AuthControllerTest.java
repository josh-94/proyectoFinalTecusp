package com.grupo10.identity.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo10.identity.application.port.in.AutenticarUseCase;
import com.grupo10.identity.application.port.in.RefrescarTokenUseCase;
import com.grupo10.identity.application.port.in.RegistrarUsuarioUseCase;
import com.grupo10.identity.domain.exception.CredencialesInvalidasException;
import com.grupo10.identity.domain.exception.UsuarioYaExisteException;
import com.grupo10.identity.domain.model.Rol;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.grupo10.identity.infrastructure.adapter.in.rest.dto.RegistrarUsuarioRequest;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AutenticarUseCase autenticarUseCase;
    @MockBean private RefrescarTokenUseCase refrescarTokenUseCase;
    @MockBean private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    @MockBean private JwtDecoder jwtDecoder;

    @Test
    void login_should_RetornarTokens_when_CredencialesValidas() throws Exception {
        when(autenticarUseCase.autenticar(any()))
                .thenReturn(new AutenticarUseCase.TokenPair("acc.tok.en", "ref.tok.en"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "pass"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("acc.tok.en"))
                .andExpect(jsonPath("$.refresh_token").value("ref.tok.en"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(900));
    }

    @Test
    void login_should_Retornar401_when_CredencialesInvalidas() throws Exception {
        when(autenticarUseCase.autenticar(any()))
                .thenThrow(new CredencialesInvalidasException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Credenciales inválidas"));
    }

    @Test
    void login_should_Retornar400_when_CuerpoVacio() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registrarUsuario_should_Retornar201_when_DatosValidos() throws Exception {
        when(registrarUsuarioUseCase.registrar(any()))
                .thenReturn(new RegistrarUsuarioUseCase.UsuarioCreado(
                        "id-1", "nuevo", "nuevo@test.com",
                        "Nuevo Usuario", Set.of(Rol.HOSPITAL_STAFF), Instant.now()));

        var request = new RegistrarUsuarioRequest(
                "nuevo", "nuevo@test.com", "Nuevo Usuario",
                "Password1!", Set.of(Rol.HOSPITAL_STAFF));

        mockMvc.perform(post("/auth/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("nuevo"))
                .andExpect(jsonPath("$.email").value("nuevo@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registrarUsuario_should_Retornar409_when_UsuarioYaExiste() throws Exception {
        when(registrarUsuarioUseCase.registrar(any()))
                .thenThrow(new UsuarioYaExisteException("admin"));

        var request = new RegistrarUsuarioRequest(
                "admin", "admin@test.com", "Admin",
                "Password1!", Set.of(Rol.ADMIN));

        mockMvc.perform(post("/auth/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_OPERATOR")
    void registrarUsuario_should_Retornar403_when_RolNoEsAdmin() throws Exception {
        var request = new RegistrarUsuarioRequest(
                "nuevo", "nuevo@test.com", "Nuevo",
                "Password1!", Set.of(Rol.HOSPITAL_STAFF));

        mockMvc.perform(post("/auth/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
