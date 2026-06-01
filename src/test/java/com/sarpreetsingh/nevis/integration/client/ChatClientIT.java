package com.sarpreetsingh.nevis.integration.client;

import com.sarpreetsingh.nevis.integration.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ChatClientIT extends AbstractIT {

    @Autowired
    private ChatClient chatClient;

    @Test
    void summarise_success() {
        String content = "this is a test content for the chat client integration test. It has dummy content.";

        String res = chatClient.prompt()
                .system(content)
                .user("summarise the content in 10 words")
                .call()
                .content();

        assertThat(res).isNotNull();
    }
}
