package com.sarpreetsingh.nevis.controller;

import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.dto.response.ClientResponse.ClientDto;
import com.sarpreetsingh.nevis.dto.response.DocumentResponse.DocumentDto;
import com.sarpreetsingh.nevis.dto.response.SearchResponse.SearchListDto;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.mapper.ClientMapper;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.mapper.DocumentMapper;
import com.sarpreetsingh.nevis.mapper.SearchResultMapper;
import com.sarpreetsingh.nevis.service.ClientDocumentEmbeddingService;
import com.sarpreetsingh.nevis.util.SearchResultWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/clients", produces = APPLICATION_JSON_VALUE)
public class ClientController {

    private final ClientDocumentEmbeddingService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ClientDto createClient(@RequestBody @Valid CreateClientDto dto) {
        ClientEntity client = service.createClient(dto);
        return ClientMapper.toDto(client);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{clientId}/documents", consumes = APPLICATION_JSON_VALUE)
    public DocumentDto createDocument(@PathVariable UUID clientId, @RequestBody @Valid CreateDocumentDto dto) {
        DocumentEntity document = service.createDocument(clientId, dto);
        return DocumentMapper.toDto(document);
    }

    @GetMapping("/search")
    public SearchListDto search(@RequestParam(name = "q", required = false) @Size(min = 1, max = 20,
                                            message = "query should be between 1 to 20 characters") String query,
                                @RequestParam(name = "limit", required = false, defaultValue = "3")
                                @Range(min = 1, max = 5, message = "limit must be from range 1 to 3") int limit) {
        SearchResultWrapper searchResultWrapper = service.search(query, limit);
        return SearchResultMapper.toDto(searchResultWrapper);
    }

    // Test endpoint to populate dummy data
    @GetMapping("/populate")
    public Map<String, String> populateData() throws MalformedURLException {
        service.populateData();
        return Map.of("status", "ok");
    }
}
