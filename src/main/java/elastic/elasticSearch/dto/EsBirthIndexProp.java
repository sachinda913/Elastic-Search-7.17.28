package elastic.elasticSearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsBirthIndexProp {
	
	@JsonProperty("id")
	public Integer id;
	
	@JsonProperty("nic_number")
	public Integer nicNumber;
	
	@JsonProperty("gender")
	public char gender;
	
	@JsonProperty("full_name")
	public String fullName;
	
	@JsonProperty("date_of_birth")
	public String dateOfBirth;


}
