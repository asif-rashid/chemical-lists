package com.example.chemicallists.membership;

import com.example.chemicallists.catalog.ListCatalogRepository;
import com.example.chemicallists.catalog.ListNotFoundException;
import com.example.chemicallists.idempotency.IdempotencyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ListMembershipService {

    private final ListCatalogRepository catalogRepository;
    private final ListMembershipRepository membershipRepository;
    private final IdempotencyService idempotencyService;

    public ListMembershipService(ListCatalogRepository catalogRepository,
                                 ListMembershipRepository membershipRepository,
                                 IdempotencyService idempotencyService) {
        this.catalogRepository = catalogRepository;
        this.membershipRepository = membershipRepository;
        this.idempotencyService = idempotencyService;
    }

    public Set<String> getMembers(long listId) {
        ensureListExists(listId);
        return membershipRepository.findMembers(listId);
    }

    public MembershipDelta addMembers(long listId, List<String> ciriIds, String idempotencyKey) {
        ensureListExists(listId);
        return idempotencyService.findPayload(idempotencyKey, MembershipDelta.class)
                .orElseGet(() -> {
                    MembershipDelta delta = membershipRepository.addMembers(listId, ciriIds);
                    idempotencyService.store(idempotencyKey, delta);
                    return delta;
                });
    }

    public void removeMember(long listId, String ciriId) {
        ensureListExists(listId);
        membershipRepository.removeMember(listId, ciriId);
    }

    public void deleteAll(long listId) {
        membershipRepository.deleteAll(listId);
    }

    private void ensureListExists(long listId) {
        catalogRepository.findById(listId).orElseThrow(() -> new ListNotFoundException(listId));
    }
}
