package com.sarpreetsingh.nevis.unit.service;

import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.service.EmbeddingService;
import com.sarpreetsingh.nevis.service.impl.EmbeddingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.UUID;

import static com.sarpreetsingh.nevis.service.impl.EmbeddingServiceImpl.ENTITY_CLASS_NAME_METADATA_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmbeddingServiceTest {

    @Mock
    VectorStore vectorStore;

    @Captor
    ArgumentCaptor<List<Document>> documentArgumentCaptor;

    @Captor
    ArgumentCaptor<SearchRequest> searchRequestArgumentCaptor;

    EmbeddingService sut;

    @BeforeEach
    void setUp() {
        sut = new EmbeddingServiceImpl(vectorStore);
    }

    @Test
    void saveClient_success() {
        ClientEntity client = buildClient();

        sut.saveClient(client);

        verify(vectorStore).add(documentArgumentCaptor.capture());

        List<Document> doc = documentArgumentCaptor.getValue();
        assertThat(doc.size()).isEqualTo(1);
        assertThat(doc.get(0).getId()).isEqualTo(client.getId().toString());
        assertThat(doc.get(0).getText()).isEqualTo(client.getFirstName() + client.getLastName()
                        + client.getEmail() + client.getDescription());
        assertThat(doc.get(0).getText()).isEqualTo(client.getFirstName() + client.getLastName()
                + client.getEmail() + client.getDescription());
        assertThat(doc.get(0).getMetadata())
                .hasFieldOrPropertyWithValue(ENTITY_CLASS_NAME_METADATA_KEY, ClientEntity.class.getName());

    }

    @Test
    void saveClient_failure_repositoryException() {
        ClientEntity client = buildClient();

        doThrow(RuntimeException.class)
                .when(vectorStore).add(any());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.saveClient(client));

        verify(vectorStore).add(anyList());
    }

    @Test
    void saveDocument_success() {
        DocumentEntity document = buildDocument();

        sut.saveDocument(document);

        verify(vectorStore).add(documentArgumentCaptor.capture());

        List<Document> doc = documentArgumentCaptor.getValue();
        assertThat(doc.size()).isEqualTo(1);
        assertThat(doc.get(0).getId()).isEqualTo(document.getId().toString());
        assertThat(doc.get(0).getText()).isEqualTo(document.getContent());
        assertThat(doc.get(0).getMetadata())
                .hasFieldOrPropertyWithValue(ENTITY_CLASS_NAME_METADATA_KEY, DocumentEntity.class.getName());
    }

    @Test
    void saveDocument_failure_repositoryException() {
        DocumentEntity document = buildDocument();

        doThrow(RuntimeException.class)
                .when(vectorStore).add(any());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.saveDocument(document));

        verify(vectorStore).add(anyList());
    }

    @Test
    void search_success() {
        String query = "query";
        int limit = 3;
        List<Document> expected = List.of();

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(expected);

        List<Document> actual = sut.search(query, limit);

        verify(vectorStore).similaritySearch(searchRequestArgumentCaptor.capture());
        assertThat(actual).isEqualTo(expected);
        assertThat(searchRequestArgumentCaptor.getValue().getQuery()).isEqualTo(query);
        assertThat(searchRequestArgumentCaptor.getValue().getTopK()).isEqualTo(limit);
    }

    @Test
    void search_failure_repositoryException() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenThrow(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.search("query", 1));

        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    ClientEntity buildClient() {
        ClientEntity client = new ClientEntity();
        client.setId(UUID.randomUUID());
        client.setFirstName("firstName");
        client.setLastName("lastName");
        client.setEmail("email");
        return client;
    }

    DocumentEntity buildDocument() {
        DocumentEntity document = new DocumentEntity();
        document.setId(UUID.randomUUID());
        document.setContent("content");
        return document;
    }
}
