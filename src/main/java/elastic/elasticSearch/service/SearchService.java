package elastic.elasticSearch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
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
            String searchFields = birthSearchDTO.getSearchFields();
            String searchValue = birthSearchDTO.getSearchValue();

            if (searchFields == null || searchFields.isEmpty() || searchValue == null || searchValue.isEmpty()) {
                return List.of();
            }
            
          //Build the Query 
            MatchQuery matchQuery = MatchQuery.of(match -> match 
                    .field(searchFields) // -> Sets the field to search
                    .query(searchValue)  // -> Sets the text for search
                    .fuzziness("AUTO") // -> Enables approximate matching
            		.operator(Operator.Or)); // Requires all terms to match -> set to "OR"  matches documents with either ex:john or smith
            
            SearchRequest request = new SearchRequest.Builder() //Create the Search Request 
                    .index(Indices.Birth_index)      // -> get the index name
                    .query(q -> q.match(matchQuery)) // -> Applies the match query as the search condition
                    .build();						 
           
         // Executing the Search
            SearchResponse<EsBirthIndexProp> response = elasticsearchClient.search(request, EsBirthIndexProp.class);

         // Map the response
            return response.hits().hits().stream()   // -> Gets the list of matching documents and creates a stream
                    .map(Hit::source)				 // -> Extracts the actual document source from each hit
                    .collect(Collectors.toList());	 // -> Gathers all documents into a List to return

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
	

	
}
