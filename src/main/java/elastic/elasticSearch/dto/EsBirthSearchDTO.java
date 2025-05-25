package elastic.elasticSearch.dto;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.Getter;

@Getter
@Service
public class EsBirthSearchDTO {

	private List<String> searchFields;
	
	private String searchValue;
	
}
