package elastic.elasticSearch.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class EsBirthSearchMultiDTO {
	
	private List<String> searchFields;
	
	private String value;

}
