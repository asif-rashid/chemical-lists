package org.ul.ciri.membership.app;

import org.springframework.stereotype.Component;
import org.ul.ciri.membership.domain.BatchMembershipResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class MembershipFacade {

    private final MembershipService service;

    public MembershipFacade(MembershipService service) {
        this.service = service;
    }

    public Set<String> getMembers(UUID listId) {
        return service.getMembers(listId);
    }

    public BatchMembershipResult addMembers(UUID listId, List<String> chemicals) {
        return service.addMembers(listId, chemicals);
    }

    public BatchMembershipResult removeMembers(UUID listId, List<String> chemicals) {
        return service.removeMembers(listId, chemicals);
    }
}
