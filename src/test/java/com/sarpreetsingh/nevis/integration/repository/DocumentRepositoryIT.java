package com.sarpreetsingh.nevis.integration.repository;

import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.integration.AbstractIT;
import com.sarpreetsingh.nevis.repository.ClientRepository;
import com.sarpreetsingh.nevis.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class DocumentRepositoryIT extends AbstractIT {

    @Autowired
    DocumentRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void saveDocument_success() {
        ClientEntity client = clientRepository.save(buildClient());
        DocumentEntity expected = buildDocument();
        expected.setClient(client);

        DocumentEntity actual = repository.save(expected);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getSummary()).isNull();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNotNull();
    }

    @Test
    void saveDocument_failure_clientNotFound() {
        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> repository.save(buildDocument()));
    }

    @Test
    void saveDocument_failure_titleNull() {
        ClientEntity client = clientRepository.save(buildClient());
        DocumentEntity expected = buildDocument();
        expected.setTitle(null);
        expected.setClient(client);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> repository.save(expected));
    }

    @Test
    void saveDocument_failure_contentNull() {
        ClientEntity client = clientRepository.save(buildClient());
        DocumentEntity expected = buildDocument();
        expected.setContent(null);
        expected.setClient(client);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> repository.save(expected));
    }

    @Test
    void findBySummaryIsNull_success() {
        ClientEntity client = clientRepository.save(buildClient());
        DocumentEntity expected = buildDocument();
        expected.setClient(client);

        repository.save(expected);

        Slice<DocumentEntity> res = repository.findBySummaryIsNull(PageRequest.of(0,  10));

        assertThat(res.getContent().isEmpty()).isFalse();
    }


    DocumentEntity buildDocument() {
        DocumentEntity document = new DocumentEntity();
        document.setTitle("title");
        document.setContent("content");
        return document;
    }

    ClientEntity buildClient() {
        ClientEntity client = new ClientEntity();
        client.setFirstName("firstName");
        client.setLastName("lastName");
        client.setEmail(UUID.randomUUID().toString());
        return client;
    }
}
