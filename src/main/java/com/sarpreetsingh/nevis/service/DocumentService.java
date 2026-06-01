package com.sarpreetsingh.nevis.service;

import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;

import java.util.Optional;
import java.util.UUID;

public interface DocumentService {

    DocumentEntity create(ClientEntity client, CreateDocumentDto dto);

    Optional<DocumentEntity> findById(UUID id);
}
