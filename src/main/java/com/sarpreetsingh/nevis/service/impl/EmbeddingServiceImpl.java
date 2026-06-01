package com.sarpreetsingh.nevis.service.impl;

import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    public static final String ENTITY_CLASS_NAME_METADATA_KEY = "entity";
    private final VectorStore vectorStore;

    @Override
    public void saveClient(ClientEntity client) {
        log.info("Saving client entity embeddings");
        StringBuilder context = new StringBuilder()
                .append(client.getFirstName())
                .append(client.getLastName())
                .append(client.getEmail())
                .append(client.getDescription());
        save(client.getId(), context, client.getClass().getName());
        log.info("Client entity embeddings saved");
    }

    @Override
    public void saveDocument(DocumentEntity document) {
        log.info("Saving document entity embeddings");
        StringBuilder context = new StringBuilder()
                .append(document.getContent());
        save(document.getId(), context, document.getClass().getName());
        log.info("Document entity embeddings saved");
    }

    @Override
    public List<Document> search(String query, int limit) {
        log.info("Searching with query [{}] and limit [{}]", query, limit);
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(query.strip())
                .topK(limit)
                .build());
        log.info("Search found [{}] items", documents.size());
        return documents;
    }

    private void save(UUID id, StringBuilder context, String entityClassName) {
        Document document = new Document(id.toString(), context.toString().strip(),
                Map.of(ENTITY_CLASS_NAME_METADATA_KEY, entityClassName));
        vectorStore.add(List.of(document));
    }
}
