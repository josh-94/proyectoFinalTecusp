package com.grupo10.inventory.application.port.out;

import com.grupo10.inventory.domain.event.StockMovimientoRegistradoEvent;

public interface PublishDomainEventPort {

    void publish(StockMovimientoRegistradoEvent event);
}
