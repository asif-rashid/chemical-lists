package org.ul.ciri.setops.app;

import org.springframework.stereotype.Component;
import org.ul.ciri.setops.domain.SetOperationRequest;
import org.ul.ciri.setops.domain.SetOperationResult;

@Component
public class SetOpsFacade {

    private final SetOpsService service;

    public SetOpsFacade(SetOpsService service) {
        this.service = service;
    }

    public SetOperationResult execute(SetOperationRequest request) {
        return service.execute(request);
    }
}
