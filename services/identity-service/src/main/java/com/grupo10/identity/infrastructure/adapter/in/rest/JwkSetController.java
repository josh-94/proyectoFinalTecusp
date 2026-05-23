package com.grupo10.identity.infrastructure.adapter.in.rest;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "JWK", description = "Clave pública RSA para validación de tokens JWT")
public class JwkSetController {

    private final JWKSource<SecurityContext> jwkSource;

    public JwkSetController(JWKSource<SecurityContext> jwkSource) {
        this.jwkSource = jwkSource;
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener el JWK Set con la clave pública RSA del servidor de identidad")
    public Map<String, Object> jwkSet() throws Exception {
        var selector = new com.nimbusds.jose.jwk.JWKSelector(
                new com.nimbusds.jose.jwk.JWKMatcher.Builder().build());
        var keys = jwkSource.get(selector, null);
        return new JWKSet(keys).toPublicJWKSet().toJSONObject();
    }
}
