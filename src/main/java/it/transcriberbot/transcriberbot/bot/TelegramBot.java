package it.transcriberbot.transcriberbot.bot;

import it.transcriberbot.transcriberbot.service.WhisperService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Bot Telegram che riceve messaggi testuali e vocali,
 * trascrive i vocali usando Whisper e risponde con il testo trascritto.
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final WhisperService whisperService;

    @Value("${telegrambots.botUsername}")
    private String botUsername;

    @Value("${telegrambots.botToken}")
    private String botToken;

    // Costruttore con log di debug
    public TelegramBot(WhisperService whisperService) {
        this.whisperService = whisperService;
        System.out.println(">>> TelegramBot istanziato correttamente <<<");
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(">>> Ricevuto un update da Telegram <<<");

        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasVoice()) {
                handleVoiceMessage(update, chatId);
            } else if (update.getMessage().hasText()) {
                handleTextMessage(update, chatId);
            }
        }
    }

    private void handleVoiceMessage(Update update, Long chatId) {
        try {
            Voice voice = update.getMessage().getVoice();
            String fileId = voice.getFileId();

            // Ottieni il file path dal server Telegram
            GetFile getFileMethod = new GetFile(fileId);
            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFileMethod);

            // Costruisci URL completo per scaricare il file
            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + telegramFile.getFilePath();

            // Scarica l'audio su file temporaneo
            java.io.File tempAudio = java.io.File.createTempFile("telegram-", ".ogg");
            try (InputStream in = new java.net.URL(fileUrl).openStream()) {
                Files.copy(in, tempAudio.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Manda messaggio di attesa
            sendMessage(chatId, "Sto trascrivendo il tuo vocale...");

            // Chiama il servizio Whisper
            whisperService.transcribe(tempAudio)
                    .doOnNext(transcribedText -> sendMessage(chatId, transcribedText))
                    .doFinally(signalType -> {
                        // Elimina il file audio scaricato dopo
                        tempAudio.delete();
                    })
                    .subscribe();

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "Si è verificato un errore durante la trascrizione del vocale.");
        }
    }

    private void handleTextMessage(Update update, Long chatId) {
        String receivedText = update.getMessage().getText();
        sendMessage(chatId, "Hai scritto: " + receivedText);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
