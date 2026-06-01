package com.sarpreetsingh.nevis.unit.service;

import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.service.SummaryService;
import com.sarpreetsingh.nevis.service.impl.SummaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;
import java.util.UUID;

import static com.sarpreetsingh.nevis.service.impl.SummaryServiceImpl.SUMMARY_PROMPT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SummaryServiceTest {

    @Mock
    ChatClient chatClient;

    @Mock
    ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    ChatClient.CallResponseSpec responseSpec;

    SummaryService sut;

    @BeforeEach
    void setUp() {
        sut = new SummaryServiceImpl(chatClient);

        when(chatClient.prompt()).thenReturn(requestSpec);
    }

    @Test
    void summarise_success() {
        DocumentEntity document = buildDocument();
        String content = toSystemContent(document);
        String expected = "test summary";

        when(requestSpec.system(content)).thenReturn(requestSpec);
        when(requestSpec.user(SUMMARY_PROMPT)).thenReturn(requestSpec);
        when(responseSpec.content()).thenReturn(expected);
        when(requestSpec.call()).thenReturn(responseSpec);

        Optional<String> actual = sut.summarise(document);

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
        verify(requestSpec).system(content);
        verify(requestSpec).user(SUMMARY_PROMPT);
        verify(responseSpec).content();
        verify(requestSpec).call();
    }

    @Test
    void summarise_failure_nullable() {
        DocumentEntity document = buildDocument();
        String content = toSystemContent(document);

        when(requestSpec.system(content)).thenReturn(requestSpec);
        when(requestSpec.user(SUMMARY_PROMPT)).thenReturn(requestSpec);
        when(responseSpec.content()).thenReturn(null);
        when(requestSpec.call()).thenReturn(responseSpec);

        Optional<String> actual = sut.summarise(document);

        assertThat(actual.isEmpty()).isTrue();
        verify(requestSpec).system(content);
        verify(requestSpec).user(SUMMARY_PROMPT);
        verify(responseSpec).content();
        verify(requestSpec).call();
    }

    @Test
    void summarise_failure_clientException() {
        DocumentEntity document = buildDocument();
        String content = toSystemContent(document);

        when(requestSpec.system(content)).thenReturn(requestSpec);
        when(requestSpec.user(SUMMARY_PROMPT)).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.summarise(document));

        verify(requestSpec).system(content);
        verify(requestSpec).user(SUMMARY_PROMPT);
        verify(requestSpec).call();
        verifyNoInteractions(responseSpec);
    }

    DocumentEntity buildDocument() {
        DocumentEntity document = new DocumentEntity();
        document.setId(UUID.randomUUID());
        document.setTitle("test title");
        document.setContent("test content");
        return document;
    }

    String toSystemContent(DocumentEntity document) {
        return document.getTitle() + " " + document.getContent();
    }
}
