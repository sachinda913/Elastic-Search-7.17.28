package elastic.elasticSearch.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import elastic.elasticSearch.dto.EsBirthIndexProp;
import elastic.elasticSearch.dto.EsBirthSearchDTO;
import elastic.elasticSearch.dto.EsBirthSearchMultiDTO;
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
            String searchFields = birthSearchDTO.getSearchField();
            String searchValue = birthSearchDTO.getSearchValue();

            if (searchFields == null || searchFields.isEmpty() || searchValue == null || searchValue.isEmpty()) {
                return List.of();
            }   
            Query query = null; 
            
         // Handle numeric fields with TermQuery
            if(searchFields.equals("id") || searchFields.equals("nic_number")) {
            	query = Query.of(q -> q
            			.term(t -> t
            					.field(searchFields)
            					.value(v -> v.longValue(Long.parseLong(searchValue)))
            					)
            			);          	
            }else {
            	 // Handle textual fields with MatchQuery
            	query = Query.of(q -> q
            			.match(m -> m
            					.field(searchFields)
            					.query(searchValue)
            					.fuzziness("AUTO")
            					.operator(Operator.Or)
            					)
            			);
            }            
          //Create the Search Request 
            SearchRequest request = new SearchRequest.Builder() 
                    .index(Indices.index_name)      // -> get the index name
                    .query(query) // -> Applies the match query as the search condition
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
        	throw new RuntimeException("Search failed due to internal error" , e);
        }
    }

    public List<EsBirthIndexProp> boolQuerySearch(Map<String, String> requestParams) {

		if(!searchParam(requestParams)) {	// -> check the	@RequestParam fields are empty	
		throw new IllegalArgumentException("Fields are Empty | at least one field need to be present");		
		}
		
		// Extract pagination parameters or use defaults
		int page = Integer.parseInt(requestParams.getOrDefault("page", "0"));  // -> get the page data or set to default for pagination
		int size = Integer.parseInt(requestParams.getOrDefault("size", "10")); // -> get the size data or set to default for pagination
		int offset = page * size;
		
		// Define fields to include in the response
		List<String> sourceFilter = Arrays.asList("id", "full_name", "gender", "nic_number", "date_of_birth");
			
		// build the BoolQuery
		BoolQuery boolQuery = boolQueryBuilder(requestParams); 
		
		// Create the Search Request 
        SearchRequest request = new SearchRequest.Builder()  
                .index(Indices.index_name) // -> get the index name
                .query(q -> q.bool(boolQuery)) // -> Applies the boolQuery query as the search condition 
                .source(s -> s  // -> Add the source filter
                		.filter(f -> f
                				.includes(sourceFilter))) 
                .sort(s -> s   // -> Add sort order ex: ASC/DESC
                		.field(f -> f
                				.field("date_of_birth")
                				.order(SortOrder.Asc)))
                .from(offset) // -> Add the offset
                .size(size) // -> Add the return data size
                .build(); 
        
    	try {
    		// Executing the Search
			SearchResponse<EsBirthIndexProp> response = elasticsearchClient.search(request, EsBirthIndexProp.class);
			
			// Map the response
	        return response.hits().hits().stream()   // -> Gets the list of matching documents and creates a stream 
	        		.map(Hit::source) 	// -> Extracts the actual document source from each hit			
	        		.collect(Collectors.toList());	 // -> Gathers all documents into a List to return
	        
		} catch (ElasticsearchException | IOException e) {
			throw new RuntimeException("Search failed due to internal error" , e);			
		}
    }
	
    // Build the BoolQuery
    private BoolQuery boolQueryBuilder(Map<String, String> requestParams) {
    	
    	// Create BoolQuery Builder
    	BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
    	
    	// Add date range filter if both 'dobfrom' and 'dobto' are present
    	 if(StringUtils.hasText(requestParams.get("dobfrom")) && StringUtils.hasText(requestParams.get("dobto"))) {
    		 
    		 // build the range query for date filter
    		 boolQueryBuilder.must(s -> s.range(r -> { r.field("date_of_birth"); // -> add should and specify the field in elastic
    		 		r.gte(JsonData.of(requestParams.get("dobfrom")));  // -> set r.get to dobfrom 
    		 		r.lte(JsonData.of(requestParams.get("dobto")));  // -> set r.get to dobto
   		 		
    		 		return r;   		 
    		 }));
    	 }
    	 // get the data to id 
    	if(StringUtils.hasText(requestParams.get("id"))) {
    		boolQueryBuilder.must(m -> m.match(t -> t.field(IndexParamToColumnMap.get("id")).query(requestParams.get("id"))));
    	}
    	
    	//  get the data to gender 
    	if(StringUtils.hasText(requestParams.get("gender"))) {
    		boolQueryBuilder.must(m -> m.match(t -> t.field(IndexParamToColumnMap.get("gender")).query(requestParams.get("gender"))));
    	}
    	
    	//  get the data to nicnumber 
    	if(StringUtils.hasText(requestParams.get("nicnumber"))) {
    		boolQueryBuilder.must(m -> m.match(t -> t.field(IndexParamToColumnMap.get("nicnumber")).query(requestParams.get("nicnumber"))));
    	} 
    	
    	// Use matchPhrasePrefix for partial match on full name
    	if(StringUtils.hasText(requestParams.get("fullname"))) {
    		boolQueryBuilder.should(s -> s.matchPhrasePrefix(t -> t.field(IndexParamToColumnMap.get("fullname")).query(requestParams.get("fullname"))));
    	}
		return boolQueryBuilder.build();
	}

    // Create the Hashmap for retire the elastic search column name using the user input field name
	private Map<String, String> getIndexParamToColumnMap() {
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("id", "id"); 
    	map.put("fullname", "full_name");  // -> pass the fullname and get the full_name column name
    	map.put("gender", "gender");
    	map.put("nicnumber", "nic_number");  // -> pass the nicnumber and get the nic_number column name
    	map.put("dateofbirth", "date_of_birth");
		return map;
	}

	// Checks whether any search-relevant parameter (other than pagination) is provided
    private boolean searchParam(Map<String, String> requestParams) {
        return requestParams.entrySet().stream()
            .anyMatch(e -> !e.getKey().equals("page") && !e.getKey().equals("size") 
            		&& e.getValue() != null && !e.getValue().isEmpty());
    }

	public List<EsBirthIndexProp> multiMatchQuerySearch(EsBirthSearchMultiDTO birthSearchMultiDTO) {
		
		List<String> searchFields = birthSearchMultiDTO.getSearchFields();
		String value = birthSearchMultiDTO.getValue();
		
		if(searchFields == null || searchFields.isEmpty() || value == null || value.isEmpty()) {
			return List.of();
		}		
		Query query;
        // Handle numeric fields with TermQuery
        if(searchFields.contains("id") || searchFields.contains("nic_number")) {
        	String fields = searchFields.get(0);
        	query = Query.of(q -> q
        			.terms(t -> t
        					.field(fields)
        					.terms(te -> te
        							.value(List.of(FieldValue.of(Long.parseLong(value)))
        									)
        							)
        					)
        			);          	
        }else {
        	 // Handle textual fields with MatchQuery
        	query = Query.of(q -> q
        			.multiMatch(m -> m
        					.fields(searchFields)
        					.query(value)
        					.fuzziness("AUTO")
        					.operator(Operator.Or)
        					)
        			);
        } 
		
		// Build the search Request
		SearchRequest searchRequest = new SearchRequest.Builder()				
				.index(Indices.index_name) // -> set the index name
				.query(query)  // -> Applies the multi match query as the search condition
				.sort(s -> s
						.field(f -> f
								.field("date_of_birth") // -> set the sort field
								.order(SortOrder.Asc))) // -> Add sort order ex: ASC/DESC
				.build();
		try {
			// Perform the search Request using client
			SearchResponse<EsBirthIndexProp> response = elasticsearchClient.search(searchRequest, EsBirthIndexProp.class);
			
	        return response.hits().hits().stream()   // -> Gets the list of matching documents and creates a stream 
	        		.map(Hit::source) 	// -> Extracts the actual document source from each hit			
	        		.collect(Collectors.toList());	 // -> Gathers all documents into a List to return
					
		} catch (ElasticsearchException | IOException e) {
			throw new RuntimeException("Search failed due to internal error" , e);	
		} 

	}
    
    
//    Clause	   Match Required	Affects Score	Use Case
//    
//    must	       Yes	            Yes	            Required conditions (AND)
//    should	   Optional	        Yes	            Optional/relevance boosting (OR)
//    must_not     Must Not	        No	            Exclude conditions
//    filter	   Yes	            No	            Structured filtering (e.g. date)
    
//  //Build the MatchQuery 											// MatchQuery can't find long values ex:id ,nic number
//  MatchQuery matchQuery = MatchQuery.of(match -> match 
//          .field(searchFields) // -> Sets the field to search
//          .query(searchValue)  // -> Sets the text for search
//          .fuzziness("AUTO") // -> Enables approximate matching
//  		.operator(Operator.Or)); // Requires all terms to match -> set to "OR"  matches documents with either ex:john or smith
	
//	// Build the MultiMatchQuery 
//	MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(match -> match
//			.fields(searchFields) // -> set the fields to search
//			.query(value) // -> set the value to search
//			.fuzziness("AUTO") // -> Enables approximate matching
//			.operator(Operator.Or)); // Requires all terms to match -> set to "OR"  matches documents with either ex:john or smith
	
}
