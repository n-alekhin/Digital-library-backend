package com.springproject.core.configuration;

import com.springproject.core.Mapper.CoverImageMapper;
import com.springproject.core.Mapper.FullBookMapper;
import jakarta.servlet.MultipartConfigElement;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
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

    factory.setMaxFileSize(DataSize.ofMegabytes(5));
    factory.setMaxRequestSize(DataSize.ofMegabytes(10));
    return factory.createMultipartConfig();
  }
}
