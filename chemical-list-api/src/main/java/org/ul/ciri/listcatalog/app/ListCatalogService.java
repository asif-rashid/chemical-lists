package org.ul.ciri.listcatalog.app;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.ul.ciri.listcatalog.data.ChemicalListRepository;
import org.ul.ciri.listcatalog.domain.ChemicalList;
import org.ul.ciri.listcatalog.domain.command.CreateListCommand;
import org.ul.ciri.listcatalog.domain.command.UpdateListCommand;
import org.ul.ciri.listcatalog.domain.events.ListCreated;
import org.ul.ciri.listcatalog.domain.events.ListDeleted;
import org.ul.ciri.listcatalog.domain.events.ListUpdated;
import org.ul.ciri.listcatalog.exception.DuplicateLabelException;
import org.ul.ciri.listcatalog.exception.ListNotFoundException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ListCatalogService {

    private final ChemicalListRepository repository;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;

    public ListCatalogService(ChemicalListRepository repository, ApplicationEventPublisher publisher, Clock clock) {
        this.repository = repository;
        this.publisher = publisher;
        this.clock = clock;
    }

    public List<ChemicalList> getAll() {
        return repository.findAll().stream().toList();
    }

    public ChemicalList get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ListNotFoundException(id));
    }

    public ChemicalList create(CreateListCommand command) {
        repository.findByLabel(command.label()).ifPresent(existing -> {
            throw new DuplicateLabelException(command.label());
        });
        Instant now = Instant.now(clock);
        ChemicalList list = new ChemicalList(UUID.randomUUID(), command.label(), command.description(), now);
        repository.save(list);
        publisher.publishEvent(new ListCreated(list.getId(), list.getLabel(), now));
        return list;
    }

    public ChemicalList update(UpdateListCommand command) {
        ChemicalList list = repository.findById(command.id()).orElseThrow(() -> new ListNotFoundException(command.id()));
        repository.findByLabel(command.label()).filter(other -> !other.getId().equals(list.getId()))
                .ifPresent(existing -> {
                    throw new DuplicateLabelException(command.label());
                });
        Instant now = Instant.now(clock);
        list.rename(command.label(), command.description(), now);
        repository.save(list);
        publisher.publishEvent(new ListUpdated(list.getId(), list.getLabel(), now));
        return list;
    }

    public void delete(UUID id) {
        ChemicalList list = repository.findById(id).orElseThrow(() -> new ListNotFoundException(id));
        repository.delete(id);
        publisher.publishEvent(new ListDeleted(list.getId(), Instant.now(clock)));
    }
}
