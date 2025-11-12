package com.example.chemicallists.api;

import com.example.chemicallists.catalog.ChemicalList;
import com.example.chemicallists.catalog.ListCatalogService;
import com.example.chemicallists.membership.ListMembershipService;
import com.example.chemicallists.membership.MembershipDelta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lists")
@Validated
public class ChemicalListController {

    private final ListCatalogService listCatalogService;
    private final ListMembershipService listMembershipService;

    public ChemicalListController(ListCatalogService listCatalogService,
                                  ListMembershipService listMembershipService) {
        this.listCatalogService = listCatalogService;
        this.listMembershipService = listMembershipService;
    }

    @GetMapping
    public List<ChemicalListResponse> getLists() {
        return listCatalogService.findAll().stream()
                .map(ChemicalListResponse::fromDomain)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ChemicalListResponse> createList(@RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
                                                           @Valid @RequestBody CreateChemicalListRequest request) {
        ChemicalList created = listCatalogService.createList(idempotencyKey, request.label(), request.name(), request.description());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getListId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(ChemicalListResponse.fromDomain(created));
    }

    @GetMapping("/{listId}")
    public ChemicalListResponse getList(@PathVariable long listId) {
        ChemicalList list = listCatalogService.findById(listId)
                .orElseThrow(() -> new com.example.chemicallists.catalog.ListNotFoundException(listId));
        return ChemicalListResponse.fromDomain(list);
    }

    @PatchMapping("/{listId}")
    public ChemicalListResponse updateList(@RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
                                           @PathVariable long listId,
                                           @Valid @RequestBody UpdateChemicalListRequest request) {
        if (request.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        ChemicalList updated = listCatalogService.updateList(idempotencyKey, listId, request.label(), request.name(), request.description());
        return ChemicalListResponse.fromDomain(updated);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteList(@PathVariable long listId) {
        listCatalogService.delete(listId);
        listMembershipService.deleteAll(listId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{listId}/chemicals")
    public Set<String> getChemicals(@PathVariable long listId) {
        return listMembershipService.getMembers(listId);
    }

    @PostMapping("/{listId}/chemicals")
    public ResponseEntity<BatchResultResponse> addChemicals(@RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
                                                            @PathVariable long listId,
                                                            @RequestBody List<@NotBlank String> ciriIds) {
        if (ciriIds == null || ciriIds.isEmpty()) {
            throw new IllegalArgumentException("At least one CIRI-ID must be provided");
        }
        MembershipDelta delta = listMembershipService.addMembers(listId, ciriIds, idempotencyKey);
        BatchResultResponse response = new BatchResultResponse(delta.added(), delta.skipped());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, currentRequest().path("/{id}").buildAndExpand(listId).toUriString())
                .body(response);
    }

    @DeleteMapping("/{listId}/chemicals/{ciriId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeChemical(@PathVariable long listId, @PathVariable String ciriId) {
        listMembershipService.removeMember(listId, ciriId);
    }

    private ServletUriComponentsBuilder currentRequest() {
        return ServletUriComponentsBuilder.fromCurrentRequest();
    }
}
