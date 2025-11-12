package com.example.chemicallists.catalog;

import com.example.chemicallists.common.ActorProvider;
import com.example.chemicallists.idempotency.IdempotencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ListCatalogService {

    private final ListCatalogRepository repository;
    private final ListIdGenerator idGenerator;
    private final Clock clock;
    private final IdempotencyService idempotencyService;
    private final ActorProvider actorProvider;

    public ListCatalogService(ListCatalogRepository repository,
                              ListIdGenerator idGenerator,
                              Clock clock,
                              IdempotencyService idempotencyService,
                              ActorProvider actorProvider) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.clock = clock;
        this.idempotencyService = idempotencyService;
        this.actorProvider = actorProvider;
    }

    public List<ChemicalList> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparingLong(ChemicalList::getListId))
                .toList();
    }

    public Optional<ChemicalList> findById(long listId) {
        return repository.findById(listId);
    }

    public ChemicalList createList(String idempotencyKey, String label, String name, String description) {
        Optional<ChemicalList> stored = idempotencyService.findPayload(idempotencyKey, ChemicalList.class);
        if (stored.isPresent()) {
            return stored.get();
        }

        repository.findByLabel(label).ifPresent(existing -> {
            throw new ListLabelAlreadyExistsException(label);
        });

        long id = idGenerator.nextId();
        Instant now = clock.instant();
        String actor = actorProvider.currentActor();
        ChemicalList list = new ChemicalList(id, label, name, description, actor, now, actor, now);
        repository.save(list);
        idempotencyService.store(idempotencyKey, list);
        return list;
    }

    public ChemicalList updateList(String idempotencyKey, long listId, String label, String name, String description) {
        Optional<ChemicalList> stored = idempotencyService.findPayload(idempotencyKey, ChemicalList.class);
        if (stored.isPresent()) {
            return stored.get();
        }

        ChemicalList current = repository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(listId));

        if (label != null && !label.equalsIgnoreCase(current.getLabel())) {
            repository.findByLabel(label).ifPresent(other -> {
                throw new ListLabelAlreadyExistsException(label);
            });
        }

        String actor = actorProvider.currentActor();
        Instant now = clock.instant();
        ChemicalList updated = current.withUpdatedMetadata(
                label,
                name,
                description,
                actor,
                now);
        repository.save(updated);
        idempotencyService.store(idempotencyKey, updated);
        return updated;
    }

    @Transactional
    public void delete(long listId) {
        ChemicalList current = repository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(listId));
        repository.delete(current.getListId());
    }
}
