package com.sarpreetsingh.nevis.integration.repository;

import com.sarpreetsingh.nevis.integration.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class VectorStoreRepositoryIT extends AbstractIT {

    @Autowired
    VectorStore vectorStore;

    @Test
    void save_success() {
        Document document = new Document(UUID.randomUUID().toString(), "text", Map.of("metadata", "save"));

        assertDoesNotThrow(() -> vectorStore.add(List.of(document)));
    }

    @Test
    void similaritySearch_success() {
        Document document = new Document(UUID.randomUUID().toString(), "similarity search",
                Map.of("metadata", "search"));
        vectorStore.add(List.of(document));

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .topK(1)
                .query("sim search")
                .build());

        assertThat(documents.isEmpty()).isFalse();
    }
}
