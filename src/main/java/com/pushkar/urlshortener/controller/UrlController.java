package com.pushkar.urlshortener.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pushkar.urlshortener.dto.UrlRequest;
import com.pushkar.urlshortener.dto.UrlResponse;
import com.pushkar.urlshortener.service.UrlService;

import jakarta.validation.Valid;

@RestController 
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping ("/api/urls")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        UrlResponse urlResponse = urlService.createShortUrl(urlRequest);
        return new ResponseEntity<>(urlResponse, HttpStatus.CREATED);
    }

    @GetMapping ("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }
}
