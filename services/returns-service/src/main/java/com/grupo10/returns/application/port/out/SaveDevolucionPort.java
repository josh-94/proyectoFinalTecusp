package com.grupo10.returns.application.port.out;

import com.grupo10.returns.domain.model.Devolucion;

public interface SaveDevolucionPort {
    Devolucion save(Devolucion devolucion);
}
