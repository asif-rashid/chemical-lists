package com.example.chemicallists.membership;

import java.util.Collection;
import java.util.Set;

public interface ListMembershipRepository {

    Set<String> findMembers(long listId);

    MembershipDelta addMembers(long listId, Collection<String> ciriIds);

    boolean removeMember(long listId, String ciriId);

    void deleteAll(long listId);
}
