package com.substring.blogapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.substring.blogapp.dto.ArticleRequestDto;
import com.substring.blogapp.dto.LoginRequest;
import com.substring.blogapp.models.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class ArticleControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String obtainJwtToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        return (String) responseMap.get("token");
    }

    @Test
    void testPublicSearchAndTrendingEndpoints() throws Exception {
        // Public search
        mockMvc.perform(get("/api/v1/articles/search")
                        .param("keyword", "Microservices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // Trending articles
        mockMvc.perform(get("/api/v1/articles/trending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Categories
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Tags
        mockMvc.perform(get("/api/v1/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCreateArticleWithJwtAuth() throws Exception {
        String token = obtainJwtToken("admin@blogapp.com", "admin123");

        ArticleRequestDto requestDto = ArticleRequestDto.builder()
                .title("Integration Testing with MockMvc & JWT")
                .content("Detailed testing steps for secure Spring Boot APIs.")
                .shortDesc("Spring Boot MockMvc testing guide.")
                .tags(Set.of("SpringBoot", "Java"))
                .status(Status.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/v1/articles/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Testing with MockMvc & JWT"))
                .andExpect(jsonPath("$.readingMinutes").value(1));
    }

    @Test
    void testUnauthorizedCreateArticle() throws Exception {
        ArticleRequestDto requestDto = ArticleRequestDto.builder()
                .title("Unauthorized Story")
                .content("Should fail without token.")
                .build();

        mockMvc.perform(post("/api/v1/articles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testFaviconServing() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/favicon.svg"))
                .andExpect(status().isOk());
    }
}
