package elastic.elasticSearch.dto;

import org.springframework.stereotype.Service;
import lombok.Getter;

@Getter
@Service
public class EsBirthSearchDTO {

	private String searchFields;
	
	private String searchValue;
	
}
