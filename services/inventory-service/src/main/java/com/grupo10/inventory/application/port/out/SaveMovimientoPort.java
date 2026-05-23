package com.grupo10.inventory.application.port.out;

import com.grupo10.inventory.domain.model.Lote;
import com.grupo10.inventory.domain.model.TipoMovimiento;

public interface SaveMovimientoPort {

    String saveMovimiento(Lote lote, TipoMovimiento tipo, int cantidad,
                          String referenciaExterna, String creadoPor);

    void updateLote(Lote lote);
}
