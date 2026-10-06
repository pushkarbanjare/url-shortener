package com.pushkar.urlshortener.service;

import com.pushkar.urlshortener.dto.UrlRequest;
import com.pushkar.urlshortener.dto.UrlResponse;

public interface UrlService {
    UrlResponse createShortUrl(UrlRequest urlRequest);
    String getOriginalUrl(String shortCode);
}
