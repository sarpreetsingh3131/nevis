package com.sarpreetsingh.nevis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class ClientRequest {

    @Data
    public static class CreateClientDto {

        @Size(max = 20, message = "first name should not exceed 20 characters")
        @NotBlank(message = "first name is missing")
        private String firstName;

        @Size(max = 20, message = "last name should not exceed 20 characters")
        @NotBlank(message = "last name is missing")
        private String lastName;

        @Size(max = 50, message = "email should not exceed 50 characters")
        @Email(message = "invalid email format")
        @NotBlank(message = "email is missing")
        private String email;

        private Optional<@Size(max = 250, message = "description should not exceed 250 characters") String>
        description = Optional.empty();

        private Optional<@Size(max = 5, message = "only upto 5 social links are allowed") List<URL>>
                socialLinks = Optional.empty();
    }
}
