package com.sarpreetsingh.nevis.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class OllamaConfiguration {

    @Bean
    public OllamaApi ollamaApi(@Value("${spring.ai.ollama.base-url}") String baseUrl,
                               @Value("${spring.ai.ollama.timeout}") String timeout) {
        // LLM may take long time to generate summary
        Duration duration = Duration.ofMinutes(Integer.parseInt(timeout));

        HttpClient httpClient  = HttpClient.newBuilder()
                .connectTimeout(duration)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(duration);

        JdkClientHttpConnector connector = new JdkClientHttpConnector(httpClient);
        connector.setReadTimeout(duration);

        return OllamaApi.builder()
                .baseUrl(baseUrl)
                .restClientBuilder(RestClient.builder().requestFactory(requestFactory))
                .webClientBuilder(WebClient.builder().clientConnector(connector))
                .build();
    }

    @Bean
    public OllamaChatOptions ollamaChatOptions(@Value("${spring.ai.ollama.chat.options.model}") String model) {
        return OllamaChatOptions.builder()
                .model(model)
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaApi ollamaApi, OllamaChatOptions ollamaChatOptions) {
        return ChatClient.create(OllamaChatModel.builder().ollamaApi(ollamaApi)
                .defaultOptions(ollamaChatOptions)
                .retryTemplate(new RetryTemplate(RetryPolicy.builder().maxRetries(1).build()))
                .build());
    }
}
