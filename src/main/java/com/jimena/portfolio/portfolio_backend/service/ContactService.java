package com.jimena.portfolio.portfolio_backend.service;

import com.jimena.portfolio.portfolio_backend.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ContactService {

    private final WebClient webClient;

    @Value("${app.mail.to}")
    private String to;

    @Value("${app.mail.from}")
    private String from; // ej: "Portfolio <onboarding@resend.dev>" al principio

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

    public void sendContactEmail(ContactRequest req) {

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

        webClient.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .onStatus(s -> s.isError(), resp ->
                        resp.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error enviando mail: " + body))
                )
                .toBodilessEntity()
                .block();

    }
}
