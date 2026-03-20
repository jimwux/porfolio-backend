package com.jimena.portfolio.portfolio_backend.service;

import com.jimena.portfolio.portfolio_backend.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ContactService {
    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final WebClient webClient;

    @Value("${app.mail.to}")
    private String to;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.subject-prefix}")
    private String subjectPrefix;

    public ContactService(WebClient.Builder builder,
                          @Value("${app.resend.api-key}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Falta RESEND_API_KEY (app.resend.api-key).");
        }

        this.webClient = builder
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Async("mailExecutor")
    public CompletableFuture<Void> sendContactEmail(ContactRequest req) {

        String text = """
        Nuevo mensaje desde tu Portfolio:

        Nombre: %s
        Email: %s

        Mensaje:
        %s
        """.formatted(req.name(), req.email(), req.message());

        Map<String, Object> payload = Map.of(
                "from", from,
                "to", new String[]{to},
                "subject", subjectPrefix + " " + req.name(),
                "text", text,
                "reply_to", req.email()
        );

        return webClient.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .onStatus(s -> s.isError(), resp ->
                        resp.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error enviando mail: " + body))
                )
                .toBodilessEntity()
                .then()
                .timeout(Duration.ofSeconds(8))
                .doOnSuccess(ignored -> log.info("Correo enviado correctamente para {}", req.email()))
                .doOnError(err -> log.error("Error enviando mail de contacto", err))
                .toFuture();

    }
}
