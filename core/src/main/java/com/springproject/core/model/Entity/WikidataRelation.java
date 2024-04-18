package com.springproject.core.model.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wikidata_relations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WikidataRelation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "word1_label")
  private String word1;
  @Column(name = "word1_id")
  private String wordId1;
  @Column(name = "relation_type")
  private String relationType;
  @Column(name = "word2_id")
  private String word2;
  @Column(name = "word2_label")
  private String wordId2;
}
