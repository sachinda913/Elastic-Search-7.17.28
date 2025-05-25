package elastic.elasticSearch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elastic.elasticSearch.dto.EsBirthIndexProp;
import elastic.elasticSearch.dto.EsBirthSearchDTO;
import elastic.elasticSearch.service.SearchService;

@RestController
@RequestMapping("/matchQuery")
public class MacthQueryController {
	
	private final SearchService searchService;
	
	public MacthQueryController(SearchService searchService) {
		this.searchService = searchService;		
	}
	
	@PostMapping("/search")
	public List<EsBirthIndexProp> search(@RequestBody EsBirthSearchDTO birthSearchDTO){
		return searchService.search(birthSearchDTO);
		
	}
	
	

}
