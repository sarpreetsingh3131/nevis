package com.sarpreetsingh.nevis.service.impl;

import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.repository.DocumentRepository;
import com.sarpreetsingh.nevis.service.DocumentService;
import com.sarpreetsingh.nevis.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository repository;
    private final SummaryService summaryService;

    @Override
    public DocumentEntity create(ClientEntity client, CreateDocumentDto dto) {
        log.info("Creating document entity for client [id={}]", client.getId());
        DocumentEntity document = new DocumentEntity();
        document.setClient(client);
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document = repository.save(document);
        log.info("Document [id={}] entity created for client [id={}]", document.getId(), client.getId());
        return document;
    }

    @Override
    public Optional<DocumentEntity> findById(UUID id) {
        log.info("Finding document entity [id={}]", id);
        Optional<DocumentEntity> document = repository.findById(id);
        log.info("Document [id={}] {}", id, document.isPresent() ? "found" : "not found");
        return document;
    }

    @Scheduled(fixedDelayString = "PT5M", initialDelayString = "PT1M")
    private void summariseDocuments() {
        log.info("Auto summarising documents started");
        int currentPage = 0;
        int pageSize = 10;
        Slice<DocumentEntity> slice;

        do {
            Pageable pageable = PageRequest.of(currentPage++,  pageSize);
            slice = repository.findBySummaryIsNull(pageable);
            log.info("Found [{}] documents without summary", slice.getContent().size());

            List<CompletableFuture<DocumentEntity>> futures = slice.stream()
                    .map(document -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return summaryService.summarise(document)
                                    .map(summary -> {
                                        document.setSummary(summary);
                                        return document;
                                    })
                                    .orElse(null);
                        } catch (Exception e) {
                            log.error("Error while summarising document [id={}]", document.getId());
                            return null;
                        }
                    }))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<DocumentEntity> documents = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();

            if (!documents.isEmpty()) {
                repository.saveAll(documents);
                log.info("Summarised [{}] documents", documents.size());
            }
        } while (slice.hasNext());

        log.info("Auto summarising documents finished");
    }
}
