package elastic.elasticSearch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsBirthIndexProp {
	
	public Integer id;
	public Integer nicNumber;
	public char gender;
	public String fullName;
	public String dateOfBirth;

}
