package org.ul.ciri.membership.data;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MembershipRepository {

    private final Map<UUID, Set<String>> storage = new ConcurrentHashMap<>();

    public Set<String> getMembers(UUID listId) {
        return storage.computeIfAbsent(listId, key -> ConcurrentHashMap.newKeySet());
    }

    public void replaceMembers(UUID listId, Set<String> members) {
        Set<String> copy = ConcurrentHashMap.newKeySet();
        copy.addAll(members);
        storage.put(listId, copy);
    }

    public Set<String> snapshot(UUID listId) {
        return Collections.unmodifiableSet(getMembers(listId));
    }
}
