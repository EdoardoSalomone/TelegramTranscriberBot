package it.transcriberbot.transcriberbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${transcriberbot.whisperApiUrl}")
    private String whisperApiUrl;

    @Bean
    public WebClient whisperWebClient() {
        return WebClient.builder()
                .baseUrl(whisperApiUrl)
                .build();
    }
}
