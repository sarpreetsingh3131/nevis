package com.sarpreetsingh.nevis.util;

import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class SearchResultWrapper {

    private final List<ClientSearchWrapper> clients = new ArrayList<>();
    private final List<DocumentSearchWrapper> documents = new ArrayList<>();

    public void addClient(Optional<Double> score, ClientEntity client) {
        clients.add(new ClientSearchWrapper(score, client));
    }

    public void addDocument(Optional<Double> score, DocumentEntity document) {
        documents.add(new DocumentSearchWrapper(score, document));
    }

    public record ClientSearchWrapper(Optional<Double> score, ClientEntity client) { }

    public record DocumentSearchWrapper(Optional<Double> score, DocumentEntity document) { }
}
