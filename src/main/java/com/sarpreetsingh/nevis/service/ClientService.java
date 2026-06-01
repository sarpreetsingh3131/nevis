package com.sarpreetsingh.nevis.service;

import com.sarpreetsingh.nevis.dto.request.ClientRequest.CreateClientDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClientService {

    ClientEntity create(CreateClientDto dto);

    Optional<ClientEntity> findById(UUID id);
}
