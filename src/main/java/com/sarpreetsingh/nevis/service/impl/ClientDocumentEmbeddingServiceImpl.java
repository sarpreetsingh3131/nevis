package com.sarpreetsingh.nevis.service.impl;

import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.exception.ClientNotFoundException;
import com.sarpreetsingh.nevis.service.*;
import com.sarpreetsingh.nevis.util.SearchResultWrapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sarpreetsingh.nevis.service.impl.EmbeddingServiceImpl.ENTITY_CLASS_NAME_METADATA_KEY;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClientDocumentEmbeddingServiceImpl implements ClientDocumentEmbeddingService {

    private final ClientService clientService;
    private final DocumentService documentService;
    private final EmbeddingService embeddingService;

    @Override
    public ClientEntity createClient(CreateClientDto dto) {
        ClientEntity client = clientService.create(dto);
        embeddingService.saveClient(client);
        return client;
    }

    @Override
    public DocumentEntity createDocument(UUID clientId, CreateDocumentDto dto) {
        DocumentEntity document = clientService.findById(clientId)
                .map(client -> documentService.create(client, dto))
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        embeddingService.saveDocument(document);
        return document;
    }

    @Override
    public SearchResultWrapper search(String query, int limit) {
        SearchResultWrapper searchResultWrapper = new SearchResultWrapper();

        embeddingService.search(query, limit)
                .forEach(item -> {
                    UUID id = UUID.fromString(item.getId());
                    Object entityClassName = item.getMetadata().get(ENTITY_CLASS_NAME_METADATA_KEY);
                    Optional<Double> score = formatScore(Optional.ofNullable(item.getScore()));

                    if (ClientEntity.class.getName().equals(entityClassName)) {
                        clientService.findById(id)
                                .ifPresent(client -> searchResultWrapper.addClient(score, client));
                    } else if (DocumentEntity.class.getName().equals(entityClassName)) {
                        documentService.findById(id)
                                .ifPresent(document -> searchResultWrapper.addDocument(score, document));
                    } else {
                        log.warn("Search results include unknown entity [{}]", entityClassName);
                    }
                });

        log.info("Search results include [{}] clients and [{}] documents",
                searchResultWrapper.getClients().size(), searchResultWrapper.getDocuments().size());
        return searchResultWrapper;
    }

    private Optional<Double> formatScore(Optional<Double> score) {
        return score.map(value -> Double.parseDouble(new DecimalFormat("#.##").format(value)));
    }

    @Override
    public void populateData() throws MalformedURLException {
        ClientEntity john = createClient(toClientDto("john", "noe", "john.doe@neviswealth.com",
                Optional.of("an employee at nevis company"),
                Optional.of(List.of(new URL("https://linkend.com/in/john-noe")))));

        ClientEntity sam = createClient(toClientDto("sam", "altman", "sam.altman@openai.com",
                Optional.of("CEO of openai since 2019"), Optional.empty()));

        ClientEntity mark = createClient(toClientDto("mark", "zuck", "mark.zuck@meta.com",
                Optional.of("CEO of meta that owns products like facebook, instagram and whatsapp"),
                Optional.empty()));

        ClientEntity andy = createClient(toClientDto("andy", "lee", "andy.lee5681@yahoo.com",
                Optional.empty(), Optional.empty()));


        createDocument(john.getId(), toDocumentDto("Payslip", "A payslip of with date 31 March 2026 and " +
                "total amount £4503.82. The employee name is John noe and employer name is Nevis."));

        createDocument(sam.getId(), toDocumentDto("Resarch paper", "A research of current topics within " +
                "the field of artificial intelligence. It overviews the current approaches and suggests new " +
                "techniques to build smart systems that can act like human. The research is published on 13 Jan 2024"));

        createDocument(andy.getId(), toDocumentDto("Council tax bill", "An annual council tax for " +
                "house no 59, Rimu street, London, UK. The monthly installement is £131.46."));

        createDocument(andy.getId(), toDocumentDto("TV license",   "A TV license for house no 47, " +
                "Andresson street, London, UK. The monthly installement is £27.46."));
    }

    private CreateClientDto toClientDto(String firstName, String lastName, String email, Optional<String> description,
                                Optional<List<URL>> socialLinks) {
        CreateClientDto dto = new CreateClientDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setDescription(description);
        dto.setSocialLinks(socialLinks);
        return dto;
    }

    private CreateDocumentDto toDocumentDto(String title, String content) {
        CreateDocumentDto dto = new CreateDocumentDto();
        dto.setTitle(title);
        dto.setContent(content);
        return dto;
    }
}
