package com.grupo10.returns.application.port.out;

import com.grupo10.returns.domain.event.DevolucionAprobadaEvent;

public interface PublishDevolucionEventPort {
    void publish(DevolucionAprobadaEvent event);
}
