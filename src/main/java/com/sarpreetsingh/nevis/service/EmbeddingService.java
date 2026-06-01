package com.sarpreetsingh.nevis.service;

import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import org.springframework.ai.document.Document;

import java.util.List;

public interface EmbeddingService {

    void saveClient(ClientEntity client);

    void saveDocument(DocumentEntity document);

    List<Document> search(String query, int limit);
}
