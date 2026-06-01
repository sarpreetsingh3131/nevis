package com.sarpreetsingh.nevis.service.impl;

import com.sarpreetsingh.nevis.entity.DocumentEntity;
import com.sarpreetsingh.nevis.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    public static final String SUMMARY_PROMPT = "Summarise in 15-20 words";
    private final ChatClient chatClient;

    @Override
    public Optional<String> summarise(DocumentEntity document) {
        log.info("Summarising document [id={}]", document.getId());
        String res = chatClient.prompt()
                .system(document.getTitle() + " " + document.getContent())
                .user(SUMMARY_PROMPT)
                .call()
                .content();

        return Optional.ofNullable(res)
                .map(summary -> {
                    log.info("Summarised document [id={}]", document.getId());
                    return summary;
                })
                .or(() -> {
                    log.error("Unable to summarise document [id={}]", document.getId());
                    return Optional.empty();
                });
    }
}
