package afr.iterson.impatient.controller;

import org.springframework.beans.factory.annotation.Autowired;

import afr.iterson.impatient.model.Patient;
import afr.iterson.impatient.model.WaitingQueue;
import afr.iterson.impatient.repository.MongoPatientRepo;
import afr.iterson.impatient.repository.MongoWaitingQueueRepo;

public class PatientControllerOps implements PatientSvcApi
{

	@Autowired
	MongoPatientRepo repo;
	
	@Autowired
	private WaitingQueue queue;
	
	
	/**
	 * changes the status to "Available/Currently Unavailable/ Unavailable"
	 */
	@Override
	public Patient changePatientStatus(Patient patient)
	{
		Patient p = queue.find(patient);
		if(p != null)
		{
			p.setStatus(patient.getStatus());
			p.setNotified(patient.isNotified());
		}
		return p;
	}

	/**
	 * Returns the Patient from the queue so that it can retrieve the current waiting time and status.
	 */
	
	@Override
	public Patient getStatus(String medicalRecordId)
	{
		Patient p = repo.findBymedicalRecordId(medicalRecordId);
		Patient p2 = queue.find(p); 
		if(p2 == null)
		{
			return p;
		}
		else 
			return p2;
	}

	/**
	 * Returns the patient from the repository to retrieve the upcoming appointment date;
	 */
	@Override
	public Patient getAppointmentDate(String medicalRecordId)
	{
		return repo.findBymedicalRecordId(medicalRecordId);
	}

	


	
}
