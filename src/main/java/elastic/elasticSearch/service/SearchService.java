package elastic.elasticSearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
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
                    .index(Indices.index_name)      // -> get the index name
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
                    .map(Hit::source)  // -> Extracts the actual document source from each hit
                    .collect(Collectors.toList());	 // -> Gathers all documents into a List to return

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<EsBirthIndexProp> boolQuerySearch(Map<String, String> requestParams) {

		if(!searchParam(requestParams)) {	// ->		
		throw new IllegalArgumentException("Fields are Empty | at least one field need to be present");
	}
		
		int page = Integer.parseInt(requestParams.getOrDefault("page", "0"));  // ->
		int size = Integer.parseInt(requestParams.getOrDefault("size", "10")); // ->
		
		List<String> sourceFilter = new ArrayList<>();  // ->
		sourceFilter.add("id");
		sourceFilter.add("full_name");
		sourceFilter.add("gender");
		sourceFilter.add("nic_number");
		sourceFilter.add("date_of_birth");		
		
		BoolQuery boolQuery = boolQueryBuilder(requestParams); // ->
		
		//
        SearchRequest request = new SearchRequest.Builder()  
                .index(Indices.index_name) // ->
                .query(q -> q.bool(boolQuery)) // ->
                .source(s -> s // ->
                		.filter(f -> f // ->
                				.includes(sourceFilter))) // ->
                .sort(s -> s // ->
                		.field(f -> f // ->
                				.field("date_of_birth")  // ->
                				.order(SortOrder.Asc)))  // ->
                .from(page * size) // ->
                .size(size) // ->
                .build(); 
        
    	try {
    		//
			SearchResponse<EsBirthIndexProp> response = elasticsearchClient.search(request, EsBirthIndexProp.class);
			
			//
	        return response.hits().hits().stream()  // ->
	        		.map(Hit::source) 	// ->			
	        		.collect(Collectors.toList());	 // ->
	        
		} catch (ElasticsearchException | IOException e) {
			e.printStackTrace();			
		}
        return null;
    }
	
    //
    private BoolQuery boolQueryBuilder(Map<String, String> requestParams) {
    	
    	//
    	BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
    	
    	//
    	 if(StringUtils.hasText(requestParams.get("dobfrom")) && StringUtils.hasText(requestParams.get("dobto"))) {
    		 
    		 //
    		 boolQueryBuilder.should(s -> s.range(r -> { r.field("date_of_birth");  // ->   		 
    		 if(StringUtils.hasText(requestParams.get("dobfrom"))) {
    			 r.gte(JsonData.of(requestParams.get("dobfrom"))); 
    		 }
    		 if (StringUtils.hasText(requestParams.get("dobto"))) { 
    	         r.lte(JsonData.of(requestParams.get("dobto")));           
    		 }
    		 return r;   		 
    		 }));
    	 }
    	 //
    	if(StringUtils.hasText(requestParams.get("id"))) {
    		boolQueryBuilder.must(m -> m.match(t -> t.field(IndexParamToColumnMap.get("id")).query(requestParams.get("id"))));
    	}
    	
    	//
    	if(StringUtils.hasText(requestParams.get("gender"))) {
    		boolQueryBuilder.must(m -> m.match(t -> t.field(IndexParamToColumnMap.get("gender")).query(requestParams.get("gender"))));
    	}
    	
    	//
    	if(StringUtils.hasText(requestParams.get("nicnumber"))) {
    		boolQueryBuilder.must(m -> m.match(t -> t.field(IndexParamToColumnMap.get("nicnumber")).query(requestParams.get("nicnumber"))));
    	} 
    	
    	//
    	if(StringUtils.hasText(requestParams.get("fullname"))) {
    		boolQueryBuilder.should(s -> s.matchPhrasePrefix(t -> t.field(IndexParamToColumnMap.get("fullname")).query(requestParams.get("fullname"))));
    	}
		return boolQueryBuilder.build();
	}

    //
	private Map<String, String> getIndexParamToColumnMap() {
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("id", "id"); 
    	map.put("fullname", "full_name");  // ->
    	map.put("gender", "gender");
    	map.put("nicnumber", "nic_number");  // ->
    	map.put("dateofbirth", "date_of_birth");
		return map;
	}

	//
    private boolean searchParam(Map<String, String> requestParams) {
        return requestParams.entrySet().stream()
            .anyMatch(e -> !e.getKey().equals("page") && !e.getKey().equals("size") 
            		&& e.getValue() != null && !e.getValue().isEmpty());
    }
    
    
//    Clause	   Match Required	Affects Score	Use Case
//    
//    must	       Yes	            Yes	            Required conditions (AND)
//    should	   Optional	        Yes	            Optional/relevance boosting (OR)
//    must_not     Must Not	        No	            Exclude conditions
//    filter	   Yes	            No	            Structured filtering (e.g. date)
    

	
}
