package com.substring.blogapp.config;

import com.substring.blogapp.utils.ArticleModelMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ArticleModelMapper articleModelMapper() {
        return new ArticleModelMapper();
    }
}
