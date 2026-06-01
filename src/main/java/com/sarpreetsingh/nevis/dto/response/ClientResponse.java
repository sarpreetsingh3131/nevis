package com.sarpreetsingh.nevis.dto.response;

import java.util.Optional;
import java.util.UUID;

public class ClientResponse {

    public record ClientDto(UUID id, String firstName,  String lastName, String email, String description,
                            String[] socialLinks) { }

    public record ClientSearchDto(Optional<Double> score, UUID id, String firstName, String lastName, String email,
                                  String description) { }

//    public record ClientMetadataDto(UUID id, String firstName,  String lastName, String email) { }
}
