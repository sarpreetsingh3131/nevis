package com.sarpreetsingh.nevis.integration.repository;

import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.integration.AbstractIT;
import com.sarpreetsingh.nevis.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class ClientRepositoryIT extends AbstractIT {

    @Autowired
    ClientRepository repository;

    @Test
    void save_success() {
        ClientEntity expected = buildClient();
        ClientEntity actual = repository.save(expected);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getDescription()).isNull();
        assertThat(actual.getSocialLinks()).isNull();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNotNull();
    }

    @Test
    void save_failure_duplicateEmail() {
        ClientEntity existing = repository.save(buildClient());
        ClientEntity client = buildClient();
        client.setEmail(existing.getEmail());

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> repository.save(client));
    }

    @Test
    void save_failure_firstNameNull() {
        ClientEntity client = buildClient();
        client.setFirstName(null);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> repository.save(client));
    }

    @Test
    void save_failure_lastNameNull() {
        ClientEntity client = buildClient();
        client.setLastName(null);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> repository.save(client));
    }

    @Test
    void save_failure_emailNull() {
        ClientEntity client = buildClient();
        client.setLastName(null);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> repository.save(client));
    }

    @Test
    void findById_success() {
        ClientEntity expected = repository.save(buildClient());
        Optional<ClientEntity> actual = repository.findById(expected.getId());

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void findById_failure_notFound() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void existsByEmail_success() {
        ClientEntity client = repository.save(buildClient());

        assertThat(repository.existsByEmail(client.getEmail())).isTrue();
    }

    @Test
    void existsByEmail_failure_notFound() {
        assertThat(repository.existsByEmail("unsaved-email")).isFalse();
    }

    ClientEntity buildClient() {
        ClientEntity client = new ClientEntity();
        client.setFirstName("FirstName");
        client.setLastName("LastName");
        client.setEmail(UUID.randomUUID().toString());
        return client;
    }
}
