package com.sarpreetsingh.nevis.unit.service;

import com.sarpreetsingh.nevis.dto.request.DocumentRequest.CreateDocumentDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.repository.DocumentRepository;
import com.sarpreetsingh.nevis.service.DocumentService;
import com.sarpreetsingh.nevis.service.SummaryService;
import com.sarpreetsingh.nevis.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    DocumentRepository repository;

    @Mock
    SummaryService summaryService;

    DocumentService sut;

    @BeforeEach
    void setUp() {
        sut = new DocumentServiceImpl(repository, summaryService);
    }

    @Test
    void create_success() {
        DocumentEntity expected = new DocumentEntity();

        when(repository.save(any()))
                .thenReturn(expected);

        DocumentEntity actual = sut.create(new ClientEntity(), new CreateDocumentDto());

        assertThat(actual).isEqualTo(expected);
        verify(repository).save(any());
        verifyNoInteractions(summaryService);
    }

    @Test
    void create_failure_repositoryException() {
        when(repository.save(any()))
                .thenThrow(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.create(new ClientEntity(), new CreateDocumentDto()));

        verify(repository).save(any());
        verifyNoInteractions(summaryService);
    }

    @Test
    void findById_success() {
        UUID id = UUID.randomUUID();
        DocumentEntity expected = new DocumentEntity();

        when(repository.findById(id))
                .thenReturn(Optional.of(expected));

        Optional<DocumentEntity> actual = sut.findById(id);

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
        verify(repository).findById(id);
        verifyNoInteractions(summaryService);
    }

    @Test
    void findById_failure_notFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<DocumentEntity> doc = sut.findById(id);

        assertThat(doc.isEmpty()).isTrue();
        verify(repository).findById(id);
        verifyNoInteractions(summaryService);
    }
}
