package com.springproject.core.Services.search;

import java.util.List;

public interface VectorService {
  List<Float> getVector(String inputString);
}
