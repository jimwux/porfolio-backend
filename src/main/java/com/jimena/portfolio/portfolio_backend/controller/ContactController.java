package com.jimena.portfolio.portfolio_backend.controller;

import com.jimena.portfolio.portfolio_backend.dto.ContactRequest;
import com.jimena.portfolio.portfolio_backend.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public Map<String, Object> send(@Valid @RequestBody ContactRequest req) {
        contactService.sendContactEmail(req);
        return Map.of("ok", true);
    }
}
