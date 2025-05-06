package it.transcriberbot.transcriberbot.service;


import it.transcriberbot.transcriberbot.dto.TranscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
@RequiredArgsConstructor
public class WhisperService {

    private final WebClient whisperWebClient;

    public Mono<String> transcribe(File audioFile){
        return whisperWebClient.post()
                .uri("/transcribe")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("audio",new FileSystemResource(audioFile)))
                .retrieve()
                .bodyToMono(TranscriptionResponse.class)
                .map(TranscriptionResponse::getText);
    }
}
