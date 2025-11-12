package com.example.chemicallists.catalog;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryListCatalogRepository implements ListCatalogRepository {

    private final Map<Long, ChemicalList> store = new ConcurrentHashMap<>();

    @Override
    public List<ChemicalList> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<ChemicalList> findById(long listId) {
        return Optional.ofNullable(store.get(listId));
    }

    @Override
    public Optional<ChemicalList> findByLabel(String label) {
        return store.values().stream()
                .filter(list -> list.getLabel().equalsIgnoreCase(label))
                .findFirst();
    }

    @Override
    public ChemicalList save(ChemicalList list) {
        store.put(list.getListId(), list);
        return list;
    }

    @Override
    public void delete(long listId) {
        store.remove(listId);
    }
}
