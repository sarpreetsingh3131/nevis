package com.sarpreetsingh.nevis.service;

import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.util.SearchResultWrapper;

import java.net.MalformedURLException;
import java.util.UUID;

public interface ClientDocumentEmbeddingService {

    ClientEntity createClient(CreateClientDto dto);

    DocumentEntity createDocument(UUID clientId, CreateDocumentDto dto);

    SearchResultWrapper search(String query, int limit);

    void populateData() throws MalformedURLException;
}
