 package elastic.elasticSearch.repository.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import elastic.elasticSearch.entity.elastic.EsBirthDetails;

@Repository
public interface ElasticRepo extends ElasticsearchRepository<EsBirthDetails, Integer>{


}
