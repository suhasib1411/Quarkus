package com.example;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanCardRepository implements PanacheRepository<PanCard> {
    public Uni<PanCard> findByIdentificationNumber(String id) {
        return find("identificationNumber", id).firstResult();
    }
}
