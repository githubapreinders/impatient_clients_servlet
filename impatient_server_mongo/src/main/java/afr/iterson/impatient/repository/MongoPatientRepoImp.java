package afr.iterson.impatient.repository;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import afr.iterson.impatient.model.Patient;

public class MongoPatientRepoImp 
{
	@Autowired 
	MongoTemplate template;
	
	public Collection<Patient> findPatientsForThisDay(String millislowerbound, String millisupperbound)
	{
		BasicQuery query = new BasicQuery("{appointmentDate : {$gt :  "+ millislowerbound +" } , appointmentDate : {$lt : " + millisupperbound  +"}}");
		return template.find(query, Patient.class);
	}
	
}
