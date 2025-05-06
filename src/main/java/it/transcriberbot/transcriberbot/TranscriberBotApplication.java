package it.transcriberbot.transcriberbot;

import it.transcriberbot.transcriberbot.bot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@Slf4j
public class TranscriberBotApplication {

    @Value("${telegrambots.botUsername}")
    private String botUsername;

    @Value("${telegrambots.botToken}")
    private String botToken;

    @PostConstruct
    public void logProps() {
        log.info("Spring prop botUsername: {}", botUsername);
        log.info("Spring prop botToken: {}", botToken != null ? "OK" : "NULL");
    }


    public static void main(String[] args) {
        SpringApplication.run(TranscriberBotApplication.class, args);
    }
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse() != null && e.getApiResponse().contains("404")) {
                System.out.println("Nessun webhook da rimuovere, avvio proseguito.");
            } else {
                throw e;
            }
        }
        return telegramBotsApi;
    }
}
