package com.springproject.core.Services;

import com.springproject.core.dto.InputData;
import com.springproject.core.dto.NounChunksDto;
import com.springproject.core.dto.VectorDto;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    return vectorDto.getResult();
  }

  public List<String> getNounChunks(String text) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    InputData inputData = new InputData(text);
    HttpEntity<InputData> request = new HttpEntity<>(inputData, headers);

    String url = "http://localhost:5000/NLP";

    NounChunksDto nounChunksDto = restTemplate.postForObject(url, request, NounChunksDto.class);
    return nounChunksDto.getNoun_chunks();
  }
}
