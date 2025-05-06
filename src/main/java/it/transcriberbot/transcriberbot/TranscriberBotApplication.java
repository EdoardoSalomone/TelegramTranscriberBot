package it.transcriberbot.transcriberbot;

import it.transcriberbot.transcriberbot.bot.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TranscriberBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranscriberBotApplication.class, args);
    }
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);
        System.out.println(">>> Bot registrato manualmente a Telegram <<<");
        return telegramBotsApi;
    }
}
