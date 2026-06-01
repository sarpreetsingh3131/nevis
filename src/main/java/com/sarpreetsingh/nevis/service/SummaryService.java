package com.sarpreetsingh.nevis.service;

import com.sarpreetsingh.nevis.entity.DocumentEntity;

import java.util.Optional;

public interface SummaryService {

    Optional<String> summarise(DocumentEntity document);
}
