package afr.iterson.impatient.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import afr.iterson.impatient.model.Patient;
import afr.iterson.impatient.model.StatusMessage;

@Controller
public class AdminController
{
	@Autowired
	public AdminControllerOps operations;


	@RequestMapping(value = AdminSvcApi.CHECKUSER_PATH ,method = RequestMethod.GET)
	public @ResponseBody StatusMessage checkUser(HttpServletRequest request)
	{
		if(request.isUserInRole("ROLE_ADMIN"))
		{
			return new StatusMessage(StatusMessage.messages.admin);
		}
		else
		{
			return new StatusMessage(StatusMessage.messages.patient);
		}
	}
	
	
	/**
	 * Admin Methods
	 */

	// GETTING PATIENT LIST
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	@RequestMapping(value = AdminSvcApi.IMPATIENT_SVC_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Patient> getPatientList()
	{
		return (Collection<Patient>) operations.getPatientList();
	}

	// ADDING A NEW PATIENT
	@RequestMapping(value = AdminSvcApi.NEWPATIENT_PATH, method = RequestMethod.POST)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Patient addNewPatient(@RequestBody Patient patient, HttpServletResponse response)
	{
		Patient p = operations.addNewPatient(patient);
		if (p == null)
		{
			response.setStatus(Response.SC_INTERNAL_SERVER_ERROR);
			return null;
		} else
		{
			response.setStatus(Response.SC_OK);
			return p;
		}
	}

	// EDITING AN EXISTING PATIENT
	@RequestMapping(value = AdminSvcApi.EDITPATIENT_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Patient editPatient(@RequestBody Patient patient, HttpServletResponse response)
	{
		return operations.editPatient(patient);
	}

	// STARTING THE SESSION FOR THIS DAY
	@RequestMapping(value = AdminSvcApi.SESSIONPATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Collection<Patient> startSession()
	{
		return operations.startSession();
	}

	// STOPPING THE SESSION
	@RequestMapping(value = AdminSvcApi.STOPSESSION_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody StatusMessage stopSession(HttpServletResponse response)
	{
		return operations.stopSession();
	}

	// GETTING A SINGLE PATIENT FROM THE QUEUE CONVENIENCE METHOD FOR DEBUGGING
	@RequestMapping(value = AdminSvcApi.GETFROMQUEUE_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Patient getFromQueue(@PathVariable(AdminSvcApi.MEDICALRECORDID) String medicalRecordId)
	{
		return operations.getFromQueue(medicalRecordId);
	}

	// MOVING A PATIENT UP IN THE QUEUE
	@RequestMapping(value = AdminSvcApi.MOVEUP_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Collection<Patient> moveUp(@RequestBody Patient patient)
	{
		return operations.moveUp(patient);
	}

	// MOVING A PATIENT DOWN IN THE QUEUE
	@RequestMapping(value = AdminSvcApi.MOVEDOWN_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Collection<Patient> moveDown(@RequestBody Patient patient)
	{
		return operations.moveDown(patient);
	}

	// REMOVING A PATIENT FROM THE QUEUE
	@RequestMapping(value = AdminSvcApi.REMOVEFROMSESSION_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Collection<Patient> removeFromSession(@RequestBody Patient patient)
	{
		return operations.deleteFromSession(patient);
	}

	// CHANGING THE STATUS OF A PATIENT
	@RequestMapping(value = AdminSvcApi.CHANGESTATUS_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Collection<Patient> changeStatus(@RequestBody Patient patient)
	{
		return operations.changeStatus(patient);
	}

	// CHANGING THE TREATMENT TIME AND THE AMOUNT OF MACHINES
	@RequestMapping(value = AdminSvcApi.CHANGE_SETTINGS_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody StatusMessage changeSettings(@PathVariable(AdminSvcApi.MACHINES) int howMuchMachines,
			@PathVariable(AdminSvcApi.TREATMENT_TIME) int treatmentTime)
	{
		System.out.println("Controller method: macines,treatmentime: " + howMuchMachines + "," +treatmentTime );
		return operations.changeSettings(howMuchMachines, treatmentTime);
	}

	//INSERTING A NEW PATIENT IN THE ROW
	@RequestMapping(value = AdminSvcApi.INSERTINSESSION_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Collection<Patient> insertInSession(@RequestBody Patient patient, @PathVariable(AdminSvcApi.POSITION) int position)
	{
		return operations.insertInSession(patient, position);
	}

	
	//GETTING THE PATIENTS LIST
	@RequestMapping(value = AdminSvcApi.POLLSESSION_PATH)
	@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
	public @ResponseBody Collection<Patient> pollSession()
	{
		return operations.pollSession();
	}
	
	
	/**
	 * Methods that can be used by patients
	 */

	@RequestMapping(value = AdminSvcApi.MEDICALRECORDID_SEARCH, method = RequestMethod.GET)
	public @ResponseBody Patient findPatientByMedicalRecordId(@PathVariable(AdminSvcApi.MEDICALRECORDID) String id)
	{
		return operations.findByMedicalRecordId(id);
	}

}
