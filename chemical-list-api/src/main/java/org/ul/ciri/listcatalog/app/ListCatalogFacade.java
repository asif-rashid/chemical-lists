package org.ul.ciri.listcatalog.app;

import org.springframework.stereotype.Component;
import org.ul.ciri.listcatalog.domain.ChemicalList;
import org.ul.ciri.listcatalog.domain.command.CreateListCommand;
import org.ul.ciri.listcatalog.domain.command.UpdateListCommand;

import java.util.List;
import java.util.UUID;

@Component
public class ListCatalogFacade {

    private final ListCatalogService service;

    public ListCatalogFacade(ListCatalogService service) {
        this.service = service;
    }

    public List<ChemicalList> list() {
        return service.getAll();
    }

    public ChemicalList get(UUID id) {
        return service.get(id);
    }

    public ChemicalList create(CreateListCommand command) {
        return service.create(command);
    }

    public ChemicalList update(UpdateListCommand command) {
        return service.update(command);
    }

    public void delete(UUID id) {
        service.delete(id);
    }
}
