package com.sarpreetsingh.nevis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class DocumentRequest {

    @Data
    public static class CreateDocumentDto {

        @Size(max = 50, message = "title should not exceed 50 characters")
        @NotBlank(message = "title is missing")
        private String title;

        @Size(max = 1000, message = "content should not exceed 1000 characters")
        @NotBlank(message = "content is missing")
        private String content;
    }
}
