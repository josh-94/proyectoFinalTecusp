package com.grupo10.identity.application.port.out;

import com.grupo10.identity.domain.model.Usuario;

public interface GenerarJwtPort {

    String generarAccessToken(Usuario usuario);
}
