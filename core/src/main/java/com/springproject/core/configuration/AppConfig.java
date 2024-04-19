package com.springproject.core.configuration;

import com.springproject.core.Mapper.CoverImageMapper;
import com.springproject.core.Mapper.FullBookMapper;
import jakarta.servlet.MultipartConfigElement;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.unit.DataSize;

import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@EnableAsync
public class AppConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
  {
    return restTemplateBuilder
            .setConnectTimeout(Duration.ofHours(1))
           .setReadTimeout(Duration.ofHours(1))
           .build();
  }

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.addConverter(new FullBookMapper());
    mapper.addConverter(new CoverImageMapper());
    return mapper;
  }
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();

    factory.setMaxFileSize(DataSize.ofMegabytes(10));
    factory.setMaxRequestSize(DataSize.ofMegabytes(10));
    return factory.createMultipartConfig();
  }
}
