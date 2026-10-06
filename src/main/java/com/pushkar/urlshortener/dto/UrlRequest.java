package com.pushkar.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class UrlRequest {

    @NotBlank (message = "Original URL must not be blank")
    private String originalUrl;
}
