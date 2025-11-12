package com.example.chemicallists.catalog;

import java.util.List;
import java.util.Optional;

public interface ListCatalogRepository {

    List<ChemicalList> findAll();

    Optional<ChemicalList> findById(long listId);

    Optional<ChemicalList> findByLabel(String label);

    ChemicalList save(ChemicalList list);

    void delete(long listId);
}
