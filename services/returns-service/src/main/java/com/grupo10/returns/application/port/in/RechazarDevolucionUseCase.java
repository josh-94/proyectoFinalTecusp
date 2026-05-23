package com.grupo10.returns.application.port.in;

public interface RechazarDevolucionUseCase {
    void rechazar(String devolucionId, String motivo);
}
