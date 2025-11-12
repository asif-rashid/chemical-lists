package org.ul.ciri.api.dto;

import java.util.Set;

public record BatchMembershipResponse(Set<String> added, Set<String> removed, Set<String> notFound) {
}
