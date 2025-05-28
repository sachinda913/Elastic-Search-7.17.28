package elastic.elasticSearch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elastic.elasticSearch.dto.EsBirthIndexProp;
import elastic.elasticSearch.dto.EsBirthSearchMultiDTO;
import elastic.elasticSearch.service.SearchService;

@RestController
@RequestMapping("/multimatch")
public class MultiMatchQueryController {
	
	private SearchService  searchService;
	
	private MultiMatchQueryController(SearchService searchService) {
		this.searchService = searchService;
	}

	@PostMapping("/search")
	public List<EsBirthIndexProp> multiMatch(@RequestBody EsBirthSearchMultiDTO birthSearchMultiDTO){
		return searchService.multiMatchQuerySearch(birthSearchMultiDTO);
	}

}
