package com.example.chemicallists.membership;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryListMembershipRepository implements ListMembershipRepository {

    private final Map<Long, Set<String>> memberships = new ConcurrentHashMap<>();

    @Override
    public Set<String> findMembers(long listId) {
        return memberships.getOrDefault(listId, Collections.emptySet());
    }

    @Override
    public MembershipDelta addMembers(long listId, Collection<String> ciriIds) {
        Set<String> members = memberships.computeIfAbsent(listId, ignored -> ConcurrentHashMap.newKeySet());
        List<String> added = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        for (String ciriId : ciriIds) {
            if (members.add(ciriId)) {
                added.add(ciriId);
            } else {
                skipped.add(ciriId);
            }
        }
        return new MembershipDelta(added, skipped);
    }

    @Override
    public boolean removeMember(long listId, String ciriId) {
        Set<String> members = memberships.get(listId);
        if (members == null) {
            return false;
        }
        boolean removed = members.remove(ciriId);
        if (members.isEmpty()) {
            memberships.remove(listId);
        }
        return removed;
    }

    @Override
    public void deleteAll(long listId) {
        memberships.remove(listId);
    }
}
