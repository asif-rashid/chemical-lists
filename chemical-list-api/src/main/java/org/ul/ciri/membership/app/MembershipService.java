package org.ul.ciri.membership.app;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.ul.ciri.listcatalog.app.ListCatalogService;
import org.ul.ciri.membership.data.MembershipRepository;
import org.ul.ciri.membership.domain.BatchMembershipResult;
import org.ul.ciri.membership.domain.events.ChemicalsAdded;
import org.ul.ciri.membership.domain.events.ChemicalsRemoved;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MembershipService {

    private final MembershipRepository repository;
    private final ListCatalogService listCatalogService;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;

    public MembershipService(MembershipRepository repository, ListCatalogService listCatalogService,
                             ApplicationEventPublisher publisher, Clock clock) {
        this.repository = repository;
        this.listCatalogService = listCatalogService;
        this.publisher = publisher;
        this.clock = clock;
    }

    public Set<String> getMembers(UUID listId) {
        listCatalogService.get(listId);
        return repository.snapshot(listId);
    }

    public BatchMembershipResult addMembers(UUID listId, List<String> chemicals) {
        listCatalogService.get(listId);
        Set<String> members = repository.getMembers(listId);
        Set<String> added = new HashSet<>();
        for (String chemical : chemicals) {
            if (members.add(chemical)) {
                added.add(chemical);
            }
        }
        if (!added.isEmpty()) {
            publisher.publishEvent(new ChemicalsAdded(listId, added, Instant.now(clock)));
        }
        return new BatchMembershipResult(Set.copyOf(added), Set.of(), Set.of());
    }

    public BatchMembershipResult removeMembers(UUID listId, List<String> chemicals) {
        listCatalogService.get(listId);
        Set<String> members = repository.getMembers(listId);
        Set<String> removed = new HashSet<>();
        for (String chemical : chemicals) {
            if (members.remove(chemical)) {
                removed.add(chemical);
            }
        }
        if (!removed.isEmpty()) {
            publisher.publishEvent(new ChemicalsRemoved(listId, removed, Instant.now(clock)));
        }
        return new BatchMembershipResult(Set.of(), Set.copyOf(removed), Set.of());
    }
}
