package com.springproject.core.Services.search;



import java.util.List;
import java.util.Objects;

import com.springproject.core.model.data.Constants;
import com.springproject.core.model.dto.InputData;
import com.springproject.core.model.dto.NounChunksDto;
import com.springproject.core.model.dto.RequestChunks;
import com.springproject.core.model.dto.VectorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class VectorServiceImpl implements VectorService{
  private final Constants constants;

  public List<Float> getVector(String inputString) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    InputData inputData = new InputData(inputString);
    HttpEntity<InputData> request = new HttpEntity<>(inputData, headers);

    VectorDto vectorDto = restTemplate.postForObject(constants.pythonUrl, request, VectorDto.class);
    return Objects.requireNonNull(vectorDto).getResult();
  }

  public List<String> getNounChunks(String text, boolean isVectorSearch) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    RequestChunks inputData = new RequestChunks(text, isVectorSearch);
    HttpEntity<RequestChunks> request = new HttpEntity<>(inputData, headers);

    String url = constants.pythonUrl + "NLP";

    NounChunksDto nounChunksDto = restTemplate.postForObject(url, request, NounChunksDto.class);
    return Objects.requireNonNull(nounChunksDto).getNoun_chunks();
  }
}
