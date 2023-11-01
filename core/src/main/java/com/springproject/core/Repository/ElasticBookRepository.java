package com.springproject.core.Repository;

import com.springproject.core.Entity.Elastic.ElasticBook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticBookRepository extends ElasticsearchRepository<ElasticBook, Long> {
}
