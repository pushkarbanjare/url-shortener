package com.pushkar.urlshortener.service;

import org.springframework.stereotype.Service;

import com.pushkar.urlshortener.dto.UrlRequest;
import com.pushkar.urlshortener.dto.UrlResponse;
import com.pushkar.urlshortener.entity.Url;
import com.pushkar.urlshortener.repository.UrlRepository;
import com.pushkar.urlshortener.util.Base62Encoder;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private static final String BASE_URL = "http://localhost:8080/";

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlResponse createShortUrl(UrlRequest request) {
        Url url = new Url();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortCode("PENDING");

        Url savedUrl = urlRepository.save(url);

        String shortCode = Base62Encoder.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);
        Url updatedUrl = urlRepository.save(savedUrl);

        return mapToResponse(updatedUrl);
    }

    @Override 
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode).orElseThrow(() -> new RuntimeException("Short URL not found:" + shortCode));
        return url.getOriginalUrl();
    }

    private UrlResponse mapToResponse(Url url) {
        return new UrlResponse(
            url.getId(),
            url.getOriginalUrl(),
            url.getShortCode(),
            BASE_URL + url.getShortCode(),
            url.getCreatedAt()
        );
    }
}
