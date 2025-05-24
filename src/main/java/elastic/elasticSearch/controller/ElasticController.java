package elastic.elasticSearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elastic.elasticSearch.entity.elastic.EsBirthDetails;
import elastic.elasticSearch.response.MetaDataUpdate;
import elastic.elasticSearch.service.ElasticService;

@RestController
@RequestMapping("/elastic")
public class ElasticController {
	
	@Autowired
	private ElasticService elasticService;
	
	@GetMapping("/test")
	public String testing() {
		return "Hello";		
	}
	
	@PatchMapping("/update/{id}")
	public ResponseEntity<String> updateData(@PathVariable Integer id,@RequestBody MetaDataUpdate metaDataUpdate){	
		try {
			String result = elasticService.updateData(id,metaDataUpdate);
			return ResponseEntity.ok(result);
			
		}catch(Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed: " + e.getMessage());
		}		
	}
	
	@PostMapping("/insert")
	public void save(@RequestBody EsBirthDetails birthDetails) {
		elasticService.save(birthDetails);
	}
	
	@GetMapping("/find/{id}")
	public EsBirthDetails findById(@PathVariable Integer id) {
		return elasticService.findById(id);
	}
	
	

}
