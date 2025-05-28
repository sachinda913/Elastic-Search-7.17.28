package elastic.elasticSearch.dto;

import org.springframework.stereotype.Service;
import lombok.Getter;

@Getter
@Service
public class EsBirthSearchDTO {

	private String searchField;
	
	private String searchValue;
	
}
