package com.springproject.core.Services;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;

import com.springproject.core.Repository.WikidataRelationRepository;
import com.springproject.core.Services.search.VectorService;
import com.springproject.core.model.Entity.WikidataRelation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WikidataServiceImpl implements WikidataService{
  private final VectorService vectorService;
  private final WikidataRelationRepository wikidataRelationRepository;

  public WikidataServiceImpl(VectorService vectorService,
      WikidataRelationRepository wikidataRelationRepository) {
    this.vectorService = vectorService;
    this.wikidataRelationRepository = wikidataRelationRepository;
  }

  @Override
  public String enrichWithWikidata(String in) {
    List<String> conceptList = vectorService.getNounChunks(in, false);
    List<WikidataRelation> wikidataRelationList;
    StringBuilder out = new StringBuilder();
    for (String  concept :  conceptList) {
      wikidataRelationList = wikidataRelationRepository.findByWord1(concept);
      for (WikidataRelation relation: wikidataRelationList){
        out.append(' ').append(relation.getWordId2());
      }
    }
    return out.toString();
  }

  @Override
  public List<String> enrichWithWikidataListString(String in) {
    List<String> conceptList = vectorService.getNounChunks(in, false);
    List<WikidataRelation> wikidataRelationList;
    List<String> out = new ArrayList<>(conceptList);
    for (String  concept :  conceptList) {
      wikidataRelationList = wikidataRelationRepository.findByWord1(concept);
      for (WikidataRelation relation: wikidataRelationList){
        out.add(relation.getWordId2());
      }
    }
    return out;
  }
}
