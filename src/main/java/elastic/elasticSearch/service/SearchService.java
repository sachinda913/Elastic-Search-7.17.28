package elastic.elasticSearch.service;

import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import elastic.elasticSearch.dto.EsBirthIndexProp;
import elastic.elasticSearch.dto.EsBirthSearchDTO;
import elastic.elasticSearch.helper.Indices;

@Service
public class SearchService {
	
    private final ElasticsearchClient elasticsearchClient;
    
    public SearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }
    

	public List<EsBirthIndexProp> search(EsBirthSearchDTO birthSearchDTO) {
		
		try {
			
		}catch(Exception e) {
			
		}

	

		return null;
		
		
	}
	


}
