package com.sarpreetsingh.nevis.unit.service;

import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.exception.DuplicateEmailException;
import com.sarpreetsingh.nevis.repository.ClientRepository;
import com.sarpreetsingh.nevis.service.ClientService;
import com.sarpreetsingh.nevis.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    ClientRepository repository;

    ClientService sut;

    @BeforeEach
    void setUp() {
        sut = new ClientServiceImpl(repository);
    }

    @Test
    void create_success() {
        ClientEntity expected = new ClientEntity();

        when(repository.existsByEmail(any()))
                .thenReturn(false);

        when(repository.save(any()))
                .thenReturn(expected);

        ClientEntity actual = sut.create(new CreateClientDto());

        assertThat(actual).isEqualTo(expected);
        verify(repository).existsByEmail(any());
        verify(repository).save(any());
    }

    @Test
    void create_failure_duplicateEmail() {
        when(repository.existsByEmail(any()))
                .thenReturn(true);

        assertThatExceptionOfType(DuplicateEmailException.class)
                .isThrownBy(() -> sut.create(new CreateClientDto()));

        verify(repository).existsByEmail(any());
        verify(repository, never()).save(any());
    }

    @Test
    void create_failure_repositoryException() {
        when(repository.existsByEmail(any()))
                .thenReturn(false);

        when(repository.save(any()))
                .thenThrow(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.create(new CreateClientDto()));

        verify(repository).existsByEmail(any());
        verify(repository).save(any());
    }

    @Test
    void findById_success() {
        UUID id = UUID.randomUUID();
        ClientEntity expected = new ClientEntity();

        when(repository.findById(id))
                .thenReturn(Optional.of(expected));

        Optional<ClientEntity> actual = sut.findById(id);

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
        verify(repository).findById(id);
    }

    @Test
    void findById_failure_notFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<ClientEntity> doc = sut.findById(id);

        assertThat(doc.isEmpty()).isTrue();
        verify(repository).findById(id);
    }
}
