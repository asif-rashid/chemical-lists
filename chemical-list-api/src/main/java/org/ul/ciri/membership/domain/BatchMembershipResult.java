package org.ul.ciri.membership.domain;

import java.util.Set;

public record BatchMembershipResult(Set<String> added, Set<String> removed, Set<String> notFound) {
}
