package elastic.elasticSearch.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import elastic.elasticSearch.entity.BirthCertificate;

@Transactional
public interface BirthCertificateCrudRepo extends JpaRepository<BirthCertificate, Long>{

	BirthCertificate findById(int id);

}
