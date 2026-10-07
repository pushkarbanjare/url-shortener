package com.pushkar.urlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pushkar.urlshortener.dto.UrlRequest;
import com.pushkar.urlshortener.dto.UrlResponse;
import com.pushkar.urlshortener.entity.Url;
import com.pushkar.urlshortener.exception.ShortUrlNotFoundException;
import com.pushkar.urlshortener.repository.UrlRepository;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {
    
    @Mock 
    private UrlRepository urlRepository;

    @InjectMocks 
    private UrlServiceImpl urlService;

    @Test 
    void createShortUrl_shouldGenerateCorrectBase62Code() {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://www.example.com");

        Url savedWithId = new Url();
        savedWithId.setId(125L);
        savedWithId.setOriginalUrl("https://www.example.com");
        savedWithId.setShortCode("PENDING");

        Url finalSaved = new Url();
        finalSaved.setId(125L);
        finalSaved.setOriginalUrl("https://www.example.com");
        finalSaved.setShortCode("21");

        when(urlRepository.save(any(Url.class))).thenReturn(savedWithId).thenReturn(finalSaved);

        UrlResponse response = urlService.createShortUrl(request);

        assertEquals("21", response.getShortCode());
        assertEquals("http://localhost:8080/21", response.getShortUrl());
        verify(urlRepository, times(2)).save(any(Url.class));
    }

    @Test 
    void getOriginalUrl_whenExists_shouldReturnUrl() {
        Url url = new Url();
        url.setShortCode("21");
        url.setOriginalUrl("https://www.example.com");

        when(urlRepository.findByShortCode("21")).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl("21");
        assertEquals("https://www.example.com", result);
    }

    @Test 
    void getOriginalUrl_whenNotExists_shouldThrowException() {
        when(urlRepository.findByShortCode("missing")).thenReturn(Optional.empty());
        assertThrows(ShortUrlNotFoundException.class, () -> urlService.getOriginalUrl("missing"));
    }
}
