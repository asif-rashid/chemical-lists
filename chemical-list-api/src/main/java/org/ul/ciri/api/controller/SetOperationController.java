package org.ul.ciri.api.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ul.ciri.api.dto.SetOperationRequestDto;
import org.ul.ciri.api.dto.SetOperationResponse;
import org.ul.ciri.setops.app.SetOpsFacade;
import org.ul.ciri.setops.domain.SetOperationRequest;
import org.ul.ciri.setops.domain.SetOperationResult;

@RestController
@RequestMapping("/api/set-operations")
public class SetOperationController {

    private final SetOpsFacade facade;

    public SetOperationController(SetOpsFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    public SetOperationResponse operate(@Valid @RequestBody SetOperationRequestDto request) {
        SetOperationResult result = facade.execute(new SetOperationRequest(request.leftListId(), request.rightListId(),
                request.type()));
        return new SetOperationResponse(result.chemicals());
    }
}
