package org.ul.ciri.listcatalog.data;

import org.springframework.stereotype.Repository;
import org.ul.ciri.listcatalog.domain.ChemicalList;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChemicalListRepository {

    private final Map<UUID, ChemicalList> storage = new ConcurrentHashMap<>();

    public Collection<ChemicalList> findAll() {
        return storage.values();
    }

    public Optional<ChemicalList> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<ChemicalList> findByLabel(String label) {
        return storage.values().stream()
                .filter(list -> list.getLabel().equalsIgnoreCase(label))
                .findFirst();
    }

    public void save(ChemicalList list) {
        storage.put(list.getId(), list);
    }

    public void delete(UUID id) {
        storage.remove(id);
    }
}
