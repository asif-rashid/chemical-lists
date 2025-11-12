package org.ul.ciri.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.ul.ciri.api.dto.BatchMembershipResponse;
import org.ul.ciri.membership.app.MembershipFacade;
import org.ul.ciri.membership.domain.BatchMembershipResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/lists/{listId}/chemicals")
public class ChemicalMembershipController {

    private final MembershipFacade facade;

    public ChemicalMembershipController(MembershipFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public Set<String> members(@PathVariable UUID listId) {
        return facade.getMembers(listId);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BatchMembershipResponse add(@PathVariable UUID listId, @Valid @RequestBody Map<String, List<String>> payload) {
        List<String> chemicals = payload.getOrDefault("chemicals", List.of());
        BatchMembershipResult result = facade.addMembers(listId, chemicals);
        return new BatchMembershipResponse(result.added(), result.removed(), result.notFound());
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BatchMembershipResponse remove(@PathVariable UUID listId, @Valid @RequestBody Map<String, List<String>> payload) {
        List<String> chemicals = payload.getOrDefault("chemicals", List.of());
        BatchMembershipResult result = facade.removeMembers(listId, chemicals);
        return new BatchMembershipResponse(result.added(), result.removed(), result.notFound());
    }
}
