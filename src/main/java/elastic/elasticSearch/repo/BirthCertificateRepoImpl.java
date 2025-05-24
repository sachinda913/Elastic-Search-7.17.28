package elastic.elasticSearch.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import elastic.elasticSearch.entity.BirthCertificate;

@Repository
public class BirthCertificateRepoImpl implements BirthCertificateRepo{
	
	@Autowired
	private BirthCertificateCrudRepo birthCertificateCrudRepo;

	@Override
	public BirthCertificate findById(int id) {
		return birthCertificateCrudRepo.findById(id);
	}

	@Override
	public BirthCertificate save(BirthCertificate birthCertificate) {
		return birthCertificateCrudRepo.save(birthCertificate);
	}

}
