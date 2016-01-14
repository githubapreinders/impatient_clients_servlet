package afr.iterson.impatient.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import afr.iterson.impatient.controller.AdminSvcApi;
import afr.iterson.impatient.model.Patient;

//@RepositoryRestResource(path = AdminSvcApi.IMPATIENT_SVC_PATH)
public interface MongoPatientRepo extends MongoRepository<Patient,Long>
{
	public Patient findBymedicalRecordId( String medicalRecordId);
	//public Patient findBymedicalRecordId(@Param(value = AdminSvcApi.MEDICALRECORDID) String medicalRecordId);
}
