package com.springproject.core.Services.search;

import java.util.List;

public interface VectorService {
  List<Float> getVector(String inputString);

  List<String> getNounChunks(String text, boolean isVectorSearch);
}
