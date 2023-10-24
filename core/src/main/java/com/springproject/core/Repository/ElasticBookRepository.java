package com.springproject.core.Repository;

import com.springproject.core.Entity.ElasticBook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticBookRepository extends ElasticsearchRepository<ElasticBook, Long> {

}
