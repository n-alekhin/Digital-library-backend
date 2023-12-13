package com.springproject.core.Repository;

import com.springproject.core.Entity.WikidataRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WikidataRelationRepository  extends JpaRepository<WikidataRelation, Long> {
  List<WikidataRelation> findByWord1(String word1);
}
