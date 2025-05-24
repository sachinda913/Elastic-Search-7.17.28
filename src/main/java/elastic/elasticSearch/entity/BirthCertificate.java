package elastic.elasticSearch.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "birth_certificate")
public class BirthCertificate implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;
	
	@Column(name = "full_name")
	private String name;
	
	@Column(name = "date_of_birth")
	private Timestamp dateOfBirth;
	
	@Column(name = "gender")
	private char gender;
	
	@Column(name = "nic_number")
	private Integer nic;
	
	@Column(name = "update_at")
	private Timestamp updateAt;

}
