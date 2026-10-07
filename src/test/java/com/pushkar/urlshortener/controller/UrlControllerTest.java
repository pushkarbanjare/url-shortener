package com.pushkar.urlshortener.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest 
@AutoConfigureMockMvc 
public class UrlControllerTest {
    
    @Autowired 
    private MockMvc mockMvc;

    @Test
    void createShortUrl_withValidUrl_shouldReturn201() throws Exception {
        String requestBody = """
                {
                    "originalUrl": "https://www.integrationtest.com"
                }
                """;

        mockMvc.perform(post("/api/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode", notNullValue()))
                .andExpect(jsonPath("$.originalUrl", is("https://www.integrationtest.com")));
    }

    @Test
    void createShortUrl_withBlankUrl_shouldReturn400() throws Exception {
        String requestBody = """
                {
                    "originalUrl": ""
                }
                """;

        mockMvc.perform(post("/api/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.originalUrl", is("Original URL must not be blank")));
    }

    @Test
    void redirect_whenShortCodeNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/this-code-does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void createThenRedirect_shouldWorkEndToEnd() throws Exception {
        String requestBody = """
                {
                    "originalUrl": "https://www.endtoendtest.com"
                }
                """;

        String responseJson = mockMvc.perform(post("/api/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String shortCode = responseJson.split("\"shortCode\":\"")[1].split("\"")[0];
        
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.endtoendtest.com"));
    }
}
