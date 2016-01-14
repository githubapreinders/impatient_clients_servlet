package afr.iterson.impatient.controller;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import afr.iterson.impatient.model.Patient;

public interface PatientSvcApi
{

	public static final String IMPATIENT_SVC_PATH = "/impatient";

	public static final String TOKEN_PATH = "/oauth/token";
	
	public static final String MEDICALRECORDID = "medicalrecordid";
	
	
	public static final String CHANGESTATUS_PATH = IMPATIENT_SVC_PATH + "/patientchangeshisstatus";
	
	public static final String GETSTATUS_PATH = IMPATIENT_SVC_PATH + "/patientgetsstatus/{" + MEDICALRECORDID +"}";
	
	public static final String GETAPPOINTMENTDATE_PATH = IMPATIENT_SVC_PATH + "/getappointmentdate/{" + MEDICALRECORDID +"}";
	
	
	//Used for a patient to check in, extend waiting time, or to check out
	@POST(CHANGESTATUS_PATH)
	public Patient changePatientStatus(@Body Patient patient);
	
	//Used for polling the server about current waiting time
	@GET(GETSTATUS_PATH)
	public Patient getStatus(@Path(MEDICALRECORDID) String medicalRecordId);
	
	//Used to retrieve the upcoming appointment date.
	@GET(GETAPPOINTMENTDATE_PATH)
	public Patient getAppointmentDate(@Path(MEDICALRECORDID) String medicalRecordId);
	
	
	
}
