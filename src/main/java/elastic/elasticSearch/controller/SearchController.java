package elastic.elasticSearch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elastic.elasticSearch.dto.BirthDetailsDto;
import elastic.elasticSearch.entity.BirthCertificate;
import elastic.elasticSearch.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {
	
	private SearchService searchService;
	
	@PostMapping("/birthCertificate")
	public List<BirthCertificate> search (@RequestBody BirthDetailsDto birthDetailsDto){
		return searchService.searchEsBirth(birthDetailsDto);			
	}
	
}
