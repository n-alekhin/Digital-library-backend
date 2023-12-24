package com.springproject.core.Services;

import java.util.List;

public interface WikidataService {
  String enrichWithWikidata(String in);
  List<String> enrichWithWikidataListString(String in);
}
