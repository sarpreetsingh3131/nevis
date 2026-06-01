package com.sarpreetsingh.nevis.mapper;

import com.sarpreetsingh.nevis.dto.response.DocumentResponse.DocumentDto;
import com.sarpreetsingh.nevis.dto.response.DocumentResponse.DocumentSearchDto;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.util.SearchResultWrapper.DocumentSearchWrapper;

import java.util.List;
import java.util.Optional;

public class DocumentMapper {

    public static DocumentDto toDto(DocumentEntity document) {
        return new DocumentDto(document.getId(), document.getClient().getId(), document.getTitle(),
                document.getContent(), document.getCreatedAt());
    }

    public static DocumentSearchDto toSearchDto(DocumentSearchWrapper searchWrapper) {
        return new DocumentSearchDto(searchWrapper.score(), searchWrapper.document().getId(),
                searchWrapper.document().getTitle(), searchWrapper.document().getSummary(),
                searchWrapper.document().getCreatedAt());
    }

    public static List<DocumentSearchDto> toListSearchDto(List<DocumentSearchWrapper> documents) {
        return Optional.ofNullable(documents)
                .orElseGet(List::of)
                .stream()
                .map(DocumentMapper::toSearchDto)
                .toList();
    }
}
