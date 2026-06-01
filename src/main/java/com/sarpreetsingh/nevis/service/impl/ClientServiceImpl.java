package com.sarpreetsingh.nevis.service.impl;

import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.exception.DuplicateEmailException;
import com.sarpreetsingh.nevis.repository.ClientRepository;
import com.sarpreetsingh.nevis.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    @Override
    public ClientEntity create(CreateClientDto dto) {
        log.info("Creating client entity");

        if (repository.existsByEmail(dto.getEmail())) {
            log.warn("Client email already exists");
            throw new DuplicateEmailException();
        }

        ClientEntity client = new ClientEntity();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        dto.getDescription().ifPresent(client::setDescription);
        dto.getSocialLinks()
                .map(list -> list.stream().map(URL::toString).toArray(String[]::new))
                .ifPresent(client::setSocialLinks);

        client = repository.save(client);
        log.info("Client entity [id={}] created", client.getId());
        return client;
    }

    @Override
    public Optional<ClientEntity> findById(UUID id) {
        log.info("Finding client entity [id={}]", id);
        Optional<ClientEntity> client = repository.findById(id);
        log.info("Client [id={}] {}", id, client.isPresent() ? "found" : "not found");
        return client;
    }
}
