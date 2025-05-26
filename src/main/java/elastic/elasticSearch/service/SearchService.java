package elastic.elasticSearch.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import elastic.elasticSearch.dto.EsBirthIndexProp;
import elastic.elasticSearch.dto.EsBirthSearchDTO;
import elastic.elasticSearch.helper.Indices;


@Service
public class SearchService {
	
    private final ElasticsearchClient elasticsearchClient;
    private final Map<String, String> IndexParamToColumnMap;
    
    public SearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
        this.IndexParamToColumnMap = getIndexParamToColumnMap();
    }
    
	public List<EsBirthIndexProp> matchQuerySearch(EsBirthSearchDTO birthSearchDTO) {
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
            
          //Create the Search Request 
            SearchRequest request = new SearchRequest.Builder() 
                    .index(Indices.Birth_index)      // -> get the index name
                    .query(q -> q.match(matchQuery)) // -> Applies the match query as the search condition
                    .sort(s -> s
                    		.field(f -> f
                    				.field("date_of_birth") // -> Add sort field
                    				.order(SortOrder.Asc))) // -> Add sort order ex: ASC/DESC
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

    public List<EsBirthIndexProp> boolQuerySearch(Map<String, String> requestParams) {
    	
    	BoolQuery boolQuery = BoolQuery.of(match -> match
    			.must(m -> m.match(t -> t.field("").query("")))
    			.must(m -> m.match(t -> t.field("").query("")))
    			.must(m -> m.match(t -> t.field("").query("")))
    			.must(m -> m.match(t -> t.field("").query("")))
    			.must(m -> m.match(t -> t.field("").query("")))
    			);
    	
        SearchRequest request = new SearchRequest.Builder() 
                .index(Indices.Birth_index)      // -> get the index name
                .query(q -> q.bool(boolQuery)) // -> Applies the match query as the search condition
                .sort(s -> s
                		.field(f -> f
                				.field("date_of_birth") // -> Add sort field
                				.order(SortOrder.Asc))) // -> Add sort order ex: ASC/DESC
                .build(); 

    	try {
			SearchResponse<EsBirthIndexProp> response = elasticsearchClient.search(request, EsBirthIndexProp.class);
	        return response.hits().hits().stream() .map(Hit::source).collect(Collectors.toList());	
	        
		} catch (ElasticsearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return null;
    }

//	private BoolQueryBuilder queryBuilder(Map<String, String> requestParams) {		
//		if(!searchParam(requestParams)) {
//			throw new IllegalArgumentException("ERROR");
//		}		
//		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//		
//		boolQueryBuilder = matchText(boolQueryBuilder,requestParams,"fullName");
//		boolQueryBuilder = addMatchQueryParams(boolQueryBuilder,requestParams,"id");
//		boolQueryBuilder = addMatchQueryParams(boolQueryBuilder,requestParams,"nicNumber");
//		boolQueryBuilder = addMatchQueryParams(boolQueryBuilder,requestParams,"gender");
//		
//		return boolQueryBuilder;
//	}
	

//	private BoolQueryBuilder matchText(BoolQueryBuilder boolQueryBuilder, Map<String, String> requestParams,String param) {
//		
//		String searchValue = requestParams.get(param);
//		String fieldName = IndexParamToColumnMap.get(param);
//		
//		BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();		
//		boolQueryBuilder2.should(QueryBuilders.wildcardQuery(fieldName + ".keyword", "*" + searchValue + "*"));
//		
//		boolQueryBuilder.must(boolQueryBuilder2);		
//		return boolQueryBuilder;
//	}
//	
//	private BoolQueryBuilder addMatchQueryParams(BoolQueryBuilder boolQueryBuilder,Map<String,String> requestParams,String param){
//		if(requestParams.get(param) != null && !requestParams.get(param).isEmpty()) {
//			boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.matchQuery(IndexParamToColumnMap.get(param), requestParams.get(param)));
//		}			
//		return boolQueryBuilder;
//	}
	
    private Map<String, String> getIndexParamToColumnMap() {
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("id", "id");
    	map.put("fullName", "full_name");
    	map.put("gender", "gender");
    	map.put("nicNumber", "nic_number");
    	map.put("dateOfBirth", "date_of_birth");
		return map;
	}

    private boolean searchParam(Map<String, String> requestParams) {
        return requestParams.entrySet().stream()
            .anyMatch(e -> !e.getKey().equals("page") && !e.getKey().equals("size") 
            		&& e.getValue() != null && !e.getValue().isEmpty());
    }

	
}
