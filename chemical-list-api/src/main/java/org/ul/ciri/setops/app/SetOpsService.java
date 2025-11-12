package org.ul.ciri.setops.app;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.ul.ciri.membership.app.MembershipService;
import org.ul.ciri.setops.domain.SetOperationRequest;
import org.ul.ciri.setops.domain.SetOperationResult;
import org.ul.ciri.setops.domain.events.SetOperationCompleted;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SetOpsService {

    private final MembershipService membershipService;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;

    public SetOpsService(MembershipService membershipService, ApplicationEventPublisher publisher, Clock clock) {
        this.membershipService = membershipService;
        this.publisher = publisher;
        this.clock = clock;
    }

    public SetOperationResult execute(SetOperationRequest request) {
        Set<String> left = membershipService.getMembers(request.leftListId());
        Set<String> right = membershipService.getMembers(request.rightListId());
        Set<String> result = switch (request.type()) {
            case UNION -> union(left, right);
            case INTERSECTION -> intersection(left, right);
            case DIFFERENCE -> difference(left, right);
        };
        publisher.publishEvent(new SetOperationCompleted(request.leftListId(), request.rightListId(), request.type(),
                result, Instant.now(clock)));
        return new SetOperationResult(result);
    }

    private Set<String> union(Set<String> left, Set<String> right) {
        LinkedHashSet<String> merged = new LinkedHashSet<>(left);
        merged.addAll(right);
        return Set.copyOf(merged);
    }

    private Set<String> intersection(Set<String> left, Set<String> right) {
        return left.stream().filter(right::contains).collect(Collectors.toUnmodifiableSet());
    }

    private Set<String> difference(Set<String> left, Set<String> right) {
        return left.stream().filter(chem -> !right.contains(chem)).collect(Collectors.toUnmodifiableSet());
    }
}
