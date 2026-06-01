package com.sarpreetsingh.nevis.unit.service;

import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.exception.ClientNotFoundException;
import com.sarpreetsingh.nevis.service.ClientDocumentEmbeddingService;
import com.sarpreetsingh.nevis.service.ClientService;
import com.sarpreetsingh.nevis.service.DocumentService;
import com.sarpreetsingh.nevis.service.EmbeddingService;
import com.sarpreetsingh.nevis.service.impl.ClientDocumentEmbeddingServiceImpl;
import com.sarpreetsingh.nevis.util.SearchResultWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

import java.util.*;

import static com.sarpreetsingh.nevis.service.impl.EmbeddingServiceImpl.ENTITY_CLASS_NAME_METADATA_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientDocumentEmbeddingServiceTest {

    @Mock
    ClientService clientService;

    @Mock
    DocumentService documentService;

    @Mock
    EmbeddingService embeddingService;

    ClientDocumentEmbeddingService sut;

    @BeforeEach
    void setUp() {
        sut = new ClientDocumentEmbeddingServiceImpl(clientService, documentService, embeddingService);
    }

    @Test
    void createClient_success() {
        CreateClientDto dto = new CreateClientDto();
        ClientEntity expected = new ClientEntity();

        when(clientService.create(dto))
                .thenReturn(expected);

        ClientEntity actual = sut.createClient(new CreateClientDto());

        assertThat(actual).isEqualTo(expected);
        verify(clientService).create(dto);
        verify(embeddingService).saveClient(expected);
        verifyNoInteractions(documentService);
    }

    @Test
    void createClient_failure_clientServiceException() {
        CreateClientDto dto = new CreateClientDto();

        when(clientService.create(dto))
                .thenThrow(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.createClient(dto));

        verify(clientService).create(dto);
        verifyNoInteractions(embeddingService, documentService);
    }

    @Test
    void createClient_failure_embeddingServiceException() {
        CreateClientDto dto = new CreateClientDto();
        ClientEntity client = new ClientEntity();

        when(clientService.create(dto))
                .thenReturn(client);

        doThrow(RuntimeException.class)
                .when(embeddingService).saveClient(client);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.createClient(dto));

        verify(clientService).create(dto);
        verify(embeddingService).saveClient(client);
        verifyNoInteractions(documentService);
    }

    @Test
    void createDocument_success() {
        UUID clientId = UUID.randomUUID();
        CreateDocumentDto dto = new CreateDocumentDto();
        ClientEntity client = new ClientEntity();
        DocumentEntity expected = new DocumentEntity();

        when(clientService.findById(clientId))
                .thenReturn(Optional.of(client));

        when(documentService.create(client, dto))
                .thenReturn(expected);

        DocumentEntity actual = sut.createDocument(clientId, new CreateDocumentDto());

        assertThat(actual).isEqualTo(expected);
        verify(clientService).findById(clientId);
        verify(documentService).create(client, dto);
        verify(embeddingService).saveDocument(expected);
    }

    @Test
    void createDocument_failure_clientNotFoundException() {
        UUID clientId = UUID.randomUUID();

        when(clientService.findById(clientId))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ClientNotFoundException.class)
                .isThrownBy(() -> sut.createDocument(clientId, new CreateDocumentDto()));

        verify(clientService).findById(clientId);
        verifyNoInteractions(documentService, embeddingService);
    }

    @Test
    void createDocument_failure_documentServiceException() {
        UUID clientId = UUID.randomUUID();
        CreateDocumentDto dto = new CreateDocumentDto();
        ClientEntity client = new ClientEntity();

        when(clientService.findById(clientId))
                .thenReturn(Optional.of(client));

        when(documentService.create(client, dto))
                .thenThrow(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.createDocument(clientId, new CreateDocumentDto()));

        verify(clientService).findById(clientId);
        verify(documentService).create(client, dto);
        verifyNoInteractions(embeddingService);
    }

    @Test
    void createDocument_failure_embeddingServiceException() {
        UUID clientId = UUID.randomUUID();
        CreateDocumentDto dto = new CreateDocumentDto();
        ClientEntity client = new ClientEntity();
        DocumentEntity expected = new DocumentEntity();

        when(clientService.findById(clientId))
                .thenReturn(Optional.of(client));

        when(documentService.create(client, dto))
                .thenReturn(expected);

        doThrow(RuntimeException.class)
                .when(embeddingService).saveDocument(expected);

        assertThatExceptionOfType(RuntimeException.class)
               .isThrownBy(() -> sut.createDocument(clientId, new CreateDocumentDto()));

        verify(clientService).findById(clientId);
        verify(documentService).create(client, dto);
        verify(embeddingService).saveDocument(expected);
    }

    @Test
    void search_success() {
        ClientEntity client = new ClientEntity();
        client.setId(UUID.randomUUID());

        DocumentEntity document = new DocumentEntity();
        document.setId(UUID.randomUUID());

        String query = "query";
        int limit = 2;

        when(embeddingService.search(query, limit))
                .thenReturn(List.of(
                        new Document(client.getId().toString(), "content",
                                Map.of(ENTITY_CLASS_NAME_METADATA_KEY, ClientEntity.class.getName())),
                        new Document(document.getId().toString(), "content",
                                Map.of(ENTITY_CLASS_NAME_METADATA_KEY, DocumentEntity.class.getName()))));

        when(clientService.findById(client.getId()))
                .thenReturn(Optional.of(client));

        when(documentService.findById(document.getId()))
                .thenReturn(Optional.of(document));

        SearchResultWrapper res = sut.search(query, limit);

        assertThat(res).isNotNull();
        assertThat(res.getClients().size()).isEqualTo(1);
        assertThat(res.getClients().get(0).client()).isEqualTo(client);
        assertThat(res.getDocuments().size()).isEqualTo(1);
        assertThat(res.getDocuments().get(0).document()).isEqualTo(document);

        verify(embeddingService).search(query, limit);
        verify(clientService).findById(client.getId());
        verify(documentService).findById(document.getId());
    }

    @Test
    void search_failure_repositoryException() {
        String query = "query";
        int limit = 2;

        when(embeddingService.search(query, limit))
                .thenThrow(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.search(query, limit));

        verify(embeddingService).search(query, limit);
        verifyNoInteractions(documentService, clientService);
    }
}
