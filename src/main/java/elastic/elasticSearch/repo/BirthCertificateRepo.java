package elastic.elasticSearch.repo;

import org.springframework.stereotype.Service;

import elastic.elasticSearch.entity.BirthCertificate;

@Service
public interface BirthCertificateRepo {
	
	BirthCertificate findById(int id);
	
	BirthCertificate save(BirthCertificate birthCertificate);

}
