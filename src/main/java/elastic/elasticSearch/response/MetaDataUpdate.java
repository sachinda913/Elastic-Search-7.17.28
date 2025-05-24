package elastic.elasticSearch.response;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MetaDataUpdate implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private char gender;
	private Integer nic;
	private Timestamp date;

}
