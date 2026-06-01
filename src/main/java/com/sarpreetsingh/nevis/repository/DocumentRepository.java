package com.sarpreetsingh.nevis.repository;

import com.sarpreetsingh.nevis.entity.DocumentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {

    Slice<DocumentEntity> findBySummaryIsNull(Pageable pageable);
}
