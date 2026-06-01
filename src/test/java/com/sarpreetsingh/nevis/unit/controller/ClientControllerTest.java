package com.sarpreetsingh.nevis.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.sarpreetsingh.nevis.controller.ClientController;
import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.exception.ClientNotFoundException;
import com.sarpreetsingh.nevis.exception.DuplicateEmailException;
import com.sarpreetsingh.nevis.service.ClientDocumentEmbeddingService;
import com.sarpreetsingh.nevis.util.SearchResultWrapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    static final String BASE_URL = "/api/v1/clients";

    ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    @MockitoBean
    ClientDocumentEmbeddingService service;

    @Autowired
    MockMvc mockMvc;

    @Test
    void createClient_allFields_success() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setDescription(Optional.of("description"));
        dto.setSocialLinks(Optional.of(List.of(new URL("https://example.com"), new URL("https://ex.com"))));

        ClientEntity client = toClient(dto);

        when(service.createClient(any()))
                .thenReturn(client);

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(client.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(client.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(client.getLastName()))
                .andExpect(jsonPath("$.email").value(client.getEmail()))
                .andExpect(jsonPath("$.description").value(client.getDescription()))
                .andExpect(jsonPath("$.socialLinks.size()").value(client.getSocialLinks().length))
                .andExpect(jsonPath("$.socialLinks[0]").value(client.getSocialLinks()[0]))
                .andExpect(jsonPath("$.socialLinks[1]").value(client.getSocialLinks()[1]));
    }

    @Test
    void createClient_requiredFieldsOnly_success() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        ClientEntity client = toClient(dto);

        when(service.createClient(dto))
                .thenReturn(client);

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(client.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(client.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(client.getLastName()))
                .andExpect(jsonPath("$.email").value(client.getEmail()))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.socialLinks").doesNotExist());
    }

    @Test
    void createClient_failed_unsupportedContentType() throws Exception {
        CreateClientDto dto = buildCreateClientDto();

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("only application/json content type supported"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failed_unsupportedAcceptContentType() throws Exception {
        CreateClientDto dto = buildCreateClientDto();

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_XML))
                .andExpect(status().isNotAcceptable());

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_duplicateEmailException() throws Exception {
        CreateClientDto dto = buildCreateClientDto();

        when(service.createClient(dto))
                .thenThrow(new DuplicateEmailException());

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void createClient_failure_unknownRuntimeException() throws Exception {
        CreateClientDto dto = buildCreateClientDto();

        when(service.createClient(dto))
                .thenThrow(new RuntimeException());

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("internal server error"));
    }

    @Test
    void createClient_failure_firstNameMissing() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setFirstName(null);

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("first name is missing"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_lastNameMissing() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setLastName(null);

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("last name is missing"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_emailMissing() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setEmail(null);

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("email is missing"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_invalidEmailFormat() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setEmail("email");

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("invalid email format"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_firstNameSizeExceeded() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setFirstName(RandomStringUtils.randomAlphabetic(40));

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("first name should not exceed 20 characters"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_lastNameSizeExceeded() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setLastName(RandomStringUtils.randomAlphabetic(40));

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("last name should not exceed 20 characters"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_emailSizeExceeded() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setEmail(RandomStringUtils.randomAlphabetic(40) + "@google.com");

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("email should not exceed 50 characters"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_descriptionSizeExceeded() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setDescription(Optional.of(RandomStringUtils.randomAlphabetic(300)));

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("description should not exceed 250 characters"));

        verifyNoInteractions(service);
    }

    @Test
    void createClient_failure_socialLinksSizeExceeded() throws Exception {
        CreateClientDto dto = buildCreateClientDto();
        dto.setSocialLinks(Optional.of(List.of(
                new URL("https://example.com"),
                new URL("https://example.com"),
                new URL("https://example.com"),
                new URL("https://example.com"),
                new URL("https://example.com"),
                new URL("https://example.com"))));

        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("only upto 5 social links are allowed"));

        verifyNoInteractions(service);
    }

    @Test
    void createDocument_success() throws Exception {
        CreateDocumentDto dto = buildCreateDocumentDto();
        ClientEntity client = buildClient();
        DocumentEntity expected = toDocument(client, dto);

        when(service.createDocument(client.getId(), dto))
                .thenReturn(expected);

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", client.getId())
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expected.getId().toString()))
                .andExpect(jsonPath("$.clientId").value(expected.getClient().getId().toString()))
                .andExpect(jsonPath("$.title").value(expected.getTitle()))
                .andExpect(jsonPath("$.content").value(expected.getContent()))
                .andExpect(jsonPath("$.summary").doesNotExist())
                .andExpect(jsonPath("$.createdAt").value(expected.getCreatedAt().toString()));

        verify(service).createDocument(client.getId(), dto);
    }

    @Test
    void createDocument_failure_unsupportedContentType() throws Exception {
        CreateDocumentDto dto = buildCreateDocumentDto();

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", UUID.randomUUID().toString())
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.ALL_VALUE))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("only application/json content type supported"));

        verifyNoInteractions(service);
    }

    @Test
    void createDocument_failure_unsupportedAcceptContentType() throws Exception {
        CreateDocumentDto dto = buildCreateDocumentDto();

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", UUID.randomUUID().toString())
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_GRAPHQL_RESPONSE))
                .andExpect(status().isNotAcceptable());

        verifyNoInteractions(service);
    }

    @Test
    void createDocument_failure_clientNotFoundException() throws Exception {
        CreateDocumentDto dto = buildCreateDocumentDto();
        UUID clientId = UUID.randomUUID();

        when(service.createDocument(clientId, dto))
                .thenThrow(new ClientNotFoundException(clientId));

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", clientId)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("Client [id=" + clientId + "] not found"));
    }

    @Test
    void createDocument_failure_unknownRuntimeException() throws Exception {
        CreateDocumentDto dto = buildCreateDocumentDto();
        UUID clientId = UUID.randomUUID();

        when(service.createDocument(clientId, dto))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", clientId)
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("internal server error"));
    }

    @Test
    void createDocument_failure_contentMissing() throws Exception {
        CreateDocumentDto dto = new CreateDocumentDto();
        dto.setTitle("title");

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", UUID.randomUUID())
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("content is missing"));

        verifyNoInteractions(service);
    }

    @Test
    void createDocument_failure_titleMissing() throws Exception {
        CreateDocumentDto dto = new CreateDocumentDto();
        dto.setContent("content");

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", UUID.randomUUID())
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("title is missing"));

        verifyNoInteractions(service);
    }

    @Test
    void createDocument_failure_titleSizeExceeded() throws Exception {
        CreateDocumentDto dto = buildCreateDocumentDto();
        dto.setTitle(RandomStringUtils.randomAlphabetic(100));

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", UUID.randomUUID())
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("title should not exceed 50 characters"));

        verifyNoInteractions(service);
    }

    @Test
    void createDocument_failure_contentSizeExceeded() throws Exception {
        CreateDocumentDto dto = buildCreateDocumentDto();
        dto.setContent(RandomStringUtils.randomAlphabetic(3000));

        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", UUID.randomUUID())
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("content should not exceed 1000 characters"));

        verifyNoInteractions(service);
    }

    @Test
    void createDocument_failure_invalidClientId() throws Exception {
        mockMvc.perform(post(BASE_URL + "/{clientId}/documents", "1244")
                        .content(mapper.writeValueAsBytes(buildCreateDocumentDto()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("invalid clientId"));

        verifyNoInteractions(service);
    }

    @Test
    void search_success() throws Exception {
        ClientEntity client = buildClient();
        client.setDescription("description");
        DocumentEntity document = buildDocument(client);

        Optional<Double> clientScore = Optional.of(0.76);
        Optional<Double> documentScore = Optional.of(0.31);

        SearchResultWrapper searchResult = new SearchResultWrapper();
        searchResult.addClient(clientScore, client);
        searchResult.addDocument(documentScore, document);

        String query = "query";
        int limit = 3;

        when(service.search(query, limit))
                .thenReturn(searchResult);

        mockMvc.perform(get(BASE_URL + "/search")
                        .queryParam("q", query)
                        .queryParam("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.clients.size()").value(1))
                .andExpect(jsonPath("$.clients[0].score").value(clientScore.get()))
                .andExpect(jsonPath("$.clients[0].id").value(client.getId().toString()))
                .andExpect(jsonPath("$.clients[0].firstName").value(client.getFirstName()))
                .andExpect(jsonPath("$.clients[0].lastName").value(client.getLastName()))
                .andExpect(jsonPath("$.clients[0].email").value(client.getEmail()))
                .andExpect(jsonPath("$.clients[0].description").value(client.getDescription()))
                .andExpect(jsonPath("$.clients[0].socialLinks").doesNotExist())
                .andExpect(jsonPath("$.documents.size()").value(1))
                .andExpect(jsonPath("$.documents[0].score").value(documentScore.get()))
                .andExpect(jsonPath("$.documents[0].id").value(document.getId().toString()))
                .andExpect(jsonPath("$.documents[0].title").value(document.getTitle()))
                .andExpect(jsonPath("$.documents[0].content").doesNotExist())
                .andExpect(jsonPath("$.documents[0].summary").value(document.getSummary()))
                .andExpect(jsonPath("$.documents[0].createdAt").value(document.getCreatedAt().toString()));
    }

    @Test
    void search_failure_unsupportedAcceptContentType() throws Exception {
        String query = "query";
        int limit = 3;

        mockMvc.perform(get(BASE_URL + "/search")
                        .queryParam("q", query)
                        .queryParam("limit", String.valueOf(limit))
                        .accept(APPLICATION_XML))
                .andExpect(status().isNotAcceptable());

        verifyNoInteractions(service);
    }

    @Test
    void search_failure_unknownRuntimeException() throws Exception {
        String query = "query";
        int limit = 3;

        when(service.search(query, limit))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get(BASE_URL + "/search")
                        .queryParam("q", query)
                        .queryParam("limit", String.valueOf(limit)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("internal server error"));
    }

    @Test
    void search_failure_limitAboveMax() throws Exception {
        String query = "query";
        int limit = 10;

        mockMvc.perform(get(BASE_URL + "/search")
                        .queryParam("q", query)
                        .queryParam("limit", String.valueOf(limit)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("limit must be from range 1 to 3"));

        verifyNoInteractions(service);
    }

    @Test
    void search_failure_limitBelowMin() throws Exception {
        String query = "query";
        int limit = 0;

        mockMvc.perform(get(BASE_URL + "/search")
                        .queryParam("q", query)
                        .queryParam("limit", String.valueOf(limit)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("limit must be from range 1 to 3"));

        verifyNoInteractions(service);
    }

    @Test
    void search_failure_querySizeAboveMax() throws Exception {
        String query = RandomStringUtils.randomAlphabetic(100);
        int limit = 2;

        mockMvc.perform(get(BASE_URL + "/search")
                        .queryParam("q", query)
                        .queryParam("limit", String.valueOf(limit)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("query should be between 1 to 20 characters"));

        verifyNoInteractions(service);
    }

    @Test
    void search_failure_querySizeBelowMin() throws Exception {
        String query = "";
        int limit = 2;

        mockMvc.perform(get(BASE_URL + "/search")
                        .queryParam("q", query)
                        .queryParam("limit", String.valueOf(limit)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("query should be between 1 to 20 characters"));

        verifyNoInteractions(service);
    }


    CreateDocumentDto buildCreateDocumentDto() {
        CreateDocumentDto dto = new CreateDocumentDto();
        dto.setTitle("title");
        dto.setContent("content");
        return dto;
    }

    CreateClientDto buildCreateClientDto() {
        CreateClientDto dto = new CreateClientDto();
        dto.setFirstName("firstName");
        dto.setLastName("lastName");
        dto.setEmail("email@google.com");
        return dto;
    }

    ClientEntity buildClient() {
        ClientEntity client = new ClientEntity();
        client.setId(UUID.randomUUID());
        client.setFirstName("firstName");
        client.setLastName("lastName");
        client.setEmail("email");
        client.setCreatedAt(Instant.now());
        return client;
    }

    DocumentEntity buildDocument(ClientEntity client) {
        DocumentEntity document = new DocumentEntity();
        document.setId(UUID.randomUUID());
        document.setTitle("title");
        document.setContent("content");
        document.setSummary("summary");
        document.setCreatedAt(Instant.now());
        document.setUpdatedAt(Instant.now());
        document.setClient(client);
        return document;
    }

    DocumentEntity toDocument(ClientEntity client, CreateDocumentDto dto) {
        DocumentEntity document = new DocumentEntity();
        document.setId(UUID.randomUUID());
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setCreatedAt(Instant.now());
        document.setUpdatedAt(Instant.now());
        document.setClient(client);
        return document;
    }

    ClientEntity toClient(CreateClientDto dto) {
        ClientEntity client = new ClientEntity();
        client.setId(UUID.randomUUID());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        dto.getDescription().ifPresent(client::setDescription);
        dto.getSocialLinks()
                .map(list -> list.stream().map(URL::toString).toArray(String[]::new))
                .ifPresent(client::setSocialLinks);
        client.setCreatedAt(Instant.now());
        return client;
    }
}
