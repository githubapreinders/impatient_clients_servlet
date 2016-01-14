package afr.iterson.impatient.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import afr.iterson.impatient.model.Patient;
import afr.iterson.impatient.repository.MongoWaitingQueueRepo;

@Controller
public class PatientController
{

	@Autowired
	public PatientControllerOps operations;

	// Posting a check in request
	@RequestMapping(value = PatientSvcApi.CHANGESTATUS_PATH, method = RequestMethod.POST)
	public @ResponseBody Patient changeStatus(@RequestBody Patient patient, HttpServletResponse response)
	{
		Patient p = operations.changePatientStatus(patient);
		if (p == null)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		return p;
	}

	//Getting the status after a check in
	@RequestMapping(value = PatientSvcApi.GETSTATUS_PATH, method = RequestMethod.GET)
	public @ResponseBody Patient getStatus(@PathVariable(PatientSvcApi.MEDICALRECORDID) String medicalRecordId,
			HttpServletResponse response)
	{
		Patient p = operations.getStatus(medicalRecordId);
		if (p == null)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		return p;
	}

	//Getting the info from the patientrepository
	@RequestMapping(value = PatientSvcApi.GETAPPOINTMENTDATE_PATH, method = RequestMethod.GET)
	public @ResponseBody Patient getAppointmentDate(
			@PathVariable(PatientSvcApi.MEDICALRECORDID) String medicalRecordId, HttpServletResponse response)
	{
		Patient p = operations.getAppointmentDate(medicalRecordId);
		if (p == null)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return p;
	}
}
