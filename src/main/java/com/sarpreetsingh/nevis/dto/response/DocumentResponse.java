package com.sarpreetsingh.nevis.dto.response;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class DocumentResponse {

    public record DocumentDto(UUID id, UUID clientId, String title, String content, Instant createdAt) { }

    public record DocumentSearchDto(Optional<Double> score, UUID id, String title, String summary, Instant createdAt) {

    }
}
