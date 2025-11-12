package org.ul.ciri.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.ul.ciri.api.dto.ChemicalListResponse;
import org.ul.ciri.api.dto.CreateListRequest;
import org.ul.ciri.api.dto.UpdateListRequest;
import org.ul.ciri.listcatalog.app.ListCatalogFacade;
import org.ul.ciri.listcatalog.domain.ChemicalList;
import org.ul.ciri.listcatalog.domain.command.CreateListCommand;
import org.ul.ciri.listcatalog.domain.command.UpdateListCommand;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lists")
public class ChemicalListController {

    private final ListCatalogFacade facade;

    public ChemicalListController(ListCatalogFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<ChemicalListResponse> list() {
        return facade.list().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ChemicalListResponse get(@PathVariable UUID id) {
        return toResponse(facade.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChemicalListResponse create(@Valid @RequestBody CreateListRequest request) {
        ChemicalList created = facade.create(new CreateListCommand(request.label(), request.description()));
        return toResponse(created);
    }

    @PutMapping("/{id}")
    public ChemicalListResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateListRequest request) {
        ChemicalList updated = facade.update(new UpdateListCommand(id, request.label(), request.description()));
        return toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        facade.delete(id);
    }

    private ChemicalListResponse toResponse(ChemicalList list) {
        return new ChemicalListResponse(list.getId(), list.getLabel(), list.getDescription(), list.getUpdatedAt());
    }
}
