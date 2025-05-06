package it.transcriberbot.transcriberbot.controller;

import it.transcriberbot.transcriberbot.service.WhisperService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/audio")
@RequiredArgsConstructor
public class AudioController {

    private final WhisperService whisperService;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/transcribe")
    public Mono<ResponseEntity<String>> transcribeAudio (@RequestParam("file")MultipartFile file) throws IOException {
        log.info("transcribe init: creating temporary upload folder");
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);
        log.info("File transferred to temporary folder");

        return whisperService.transcribe(tempFile)
                .map(text -> {
                    log.info("transcription completed");
                    log.info("Content : " + text);
                    tempFile.delete();
                    log.info("file deleted from temporary folder");
                    return ResponseEntity.ok(text);
                });
    }
}
