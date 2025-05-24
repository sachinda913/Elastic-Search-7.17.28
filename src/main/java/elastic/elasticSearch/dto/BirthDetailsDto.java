package elastic.elasticSearch.dto;

import java.util.List;

import lombok.Data;

@Data
public class BirthDetailsDto {
	
	private List<String> fields;
	
	private String searchTearm;

}
