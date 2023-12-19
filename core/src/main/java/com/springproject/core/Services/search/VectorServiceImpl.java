package com.springproject.core.Services.search;

import com.springproject.core.dto.NounChunksDto;

import java.util.List;
import java.util.Objects;

import com.springproject.core.model.dto.InputData;
import com.springproject.core.model.dto.RequestChunks;
import com.springproject.core.model.dto.VectorDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VectorServiceImpl implements VectorService{

  public List<Float> getVector(String inputString) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    InputData inputData = new InputData(inputString);
    HttpEntity<InputData> request = new HttpEntity<>(inputData, headers);

    String url = "http://localhost:5000/";

    VectorDto vectorDto = restTemplate.postForObject(url, request, VectorDto.class);
    return Objects.requireNonNull(vectorDto).getResult();
  }

  public List<String> getNounChunks(String text, boolean isVectorSearch) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    RequestChunks inputData = new RequestChunks(text, isVectorSearch);
    HttpEntity<RequestChunks> request = new HttpEntity<>(inputData, headers);

    String url = "http://localhost:5000/NLP";

    NounChunksDto nounChunksDto = restTemplate.postForObject(url, request, NounChunksDto.class);
    return Objects.requireNonNull(nounChunksDto).getNoun_chunks();
  }
}
