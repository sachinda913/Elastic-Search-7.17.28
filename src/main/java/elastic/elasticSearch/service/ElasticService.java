package elastic.elasticSearch.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import elastic.elasticSearch.entity.BirthCertificate;
import elastic.elasticSearch.entity.elastic.EsBirthDetails;
import elastic.elasticSearch.repo.BirthCertificateRepo;
import elastic.elasticSearch.repository.elastic.ElasticRepo;
import elastic.elasticSearch.response.MetaDataUpdate;

@Service
public class ElasticService {
	
	@Autowired
	private ElasticRepo elasticRepo;
		
	@Autowired
	private BirthCertificateRepo birthCertificateRepo;

	public String updateData(Integer id, MetaDataUpdate metaDataUpdate) {
		
		try {
			BirthCertificate birthCertificate = birthCertificateRepo.findById(id);
			
			if(birthCertificate == null) {
				 return "Birth certificate with ID " + id + " not found.";				
			}			
			birthCertificate.setName(metaDataUpdate.getName());
			birthCertificate.setDateOfBirth(metaDataUpdate.getDate());
			birthCertificate.setGender(metaDataUpdate.getGender());
			birthCertificate.setNic(metaDataUpdate.getNic());
			birthCertificate.setUpdateAt(new Timestamp(System.currentTimeMillis()));
			
			birthCertificateRepo.save(birthCertificate);			
			return "Birth certificate updated successfully.";
			
		}catch(Exception e) {
	        throw new RuntimeException("Error occurred while updating birth certificate: " + e.getMessage(), e);
		}
	}
	
	public void save(EsBirthDetails birthDetails) {
		elasticRepo.save(birthDetails);
	}
	
	public EsBirthDetails findById(Integer id) {
		return elasticRepo.findById(id).orElse(null);
	}

	 	
}
