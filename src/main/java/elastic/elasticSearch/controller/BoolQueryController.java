package elastic.elasticSearch.controller;


import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import elastic.elasticSearch.dto.EsBirthIndexProp;
import elastic.elasticSearch.service.SearchService;

@RestController
@RequestMapping("/boolQuery")
public class BoolQueryController {
	
	private final SearchService searchService;
	
	public BoolQueryController(SearchService searchService) {
		this.searchService = searchService;
	}

	@GetMapping("/search")
	public List<EsBirthIndexProp> search(@RequestParam Map<String, String> requestParams){
		return searchService.boolQuerySearch(requestParams);		
	}
}
