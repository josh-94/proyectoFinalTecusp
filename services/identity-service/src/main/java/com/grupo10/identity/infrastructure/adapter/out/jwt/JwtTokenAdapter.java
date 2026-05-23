package com.grupo10.identity.infrastructure.adapter.out.jwt;

import com.grupo10.identity.application.port.out.GenerarJwtPort;
import com.grupo10.identity.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class JwtTokenAdapter implements GenerarJwtPort {

    private static final int ACCESS_TOKEN_EXPIRY_MINUTES = 15;

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.issuer:identity-service}")
    private String issuer;

    public JwtTokenAdapter(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generarAccessToken(Usuario usuario) {
        Instant now = Instant.now();

        List<String> roles = usuario.getRoles().stream()
                .map(Enum::name)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .subject(usuario.getId())
                .claim("username", usuario.getUsername())
                .claim("roles", roles)
                .build();

        var header = JwsHeader.with(() -> "RS256").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
