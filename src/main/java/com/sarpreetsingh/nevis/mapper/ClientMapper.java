package com.sarpreetsingh.nevis.mapper;

import com.sarpreetsingh.nevis.dto.response.ClientResponse.ClientDto;
import com.sarpreetsingh.nevis.dto.response.ClientResponse.ClientSearchDto;
import com.sarpreetsingh.nevis.entity.ClientEntity;
import com.sarpreetsingh.nevis.util.SearchResultWrapper.ClientSearchWrapper;

import java.util.List;
import java.util.Optional;

public class ClientMapper {

    public static ClientDto toDto(ClientEntity client) {
        return new ClientDto(client.getId(), client.getFirstName(), client.getLastName(), client.getEmail(),
                client.getDescription(), client.getSocialLinks());
    }

    public static ClientSearchDto toSearchDto(ClientSearchWrapper searchWrapper) {
        return new ClientSearchDto(searchWrapper.score(), searchWrapper.client().getId(),
                searchWrapper.client().getFirstName(), searchWrapper.client().getLastName(),
                searchWrapper.client().getEmail(), searchWrapper.client().getDescription());
    }

    public static List<ClientSearchDto> toListSearchDto(List<ClientSearchWrapper> clients) {
        return Optional.ofNullable(clients)
                .orElseGet(List::of)
                .stream()
                .map(ClientMapper::toSearchDto)
                .toList();
    }
}
