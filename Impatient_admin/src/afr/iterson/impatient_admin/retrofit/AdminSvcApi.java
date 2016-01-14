package afr.iterson.impatient_admin.retrofit;

import java.util.Collection;

import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.model.StatusMessage;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * This Api describes the methods that can be used by the admin app to access the Rest Service; At the same time this api
 * functions as an interface for controller operations. 
 * @author ap
 *
 */
public interface AdminSvcApi
{

	public static final String MEDICALRECORDID = "medicalrecordid";
	public static final String LOWERBOUND = "lowerbound";
	public static final String UPPERBOUND = "upperbound";
	public static final String MACHINES = "machines";
	public static final String TREATMENT_TIME = "treatmenttime";
	public static final String POSITION = "position";

	
	
	public static final String IMPATIENT_SVC_PATH = "/impatient";

	public static final String CHECKUSER_PATH = IMPATIENT_SVC_PATH + "/checkuser";
	public static final String NEWPATIENT_PATH = IMPATIENT_SVC_PATH + "/addnewpatient";
	public static final String SESSIONPATH = IMPATIENT_SVC_PATH + "/session";
	public static final String EDITPATIENT_PATH = IMPATIENT_SVC_PATH + "/editpatient";
	public static final String MEDICALRECORDID_SEARCH = IMPATIENT_SVC_PATH + "/findbymedicalrecordid/{" + MEDICALRECORDID + "}" ;
	public static final String CHANGE_SETTINGS_PATH = IMPATIENT_SVC_PATH 
			+ "/{"	+ MACHINES 	+"}/settings/{"	+TREATMENT_TIME	+"}";

	public static final String TOKEN_PATH = "/oauth/token";
	
	

	public static final String POLLSESSION_PATH = SESSIONPATH +  "/poll";
	public static final String MOVEUP_PATH = SESSIONPATH +  "/moveup";
	public static final String MOVEDOWN_PATH = SESSIONPATH + "/movedown";
	public static final String CHANGESTATUS_PATH = SESSIONPATH + "/adminchangespatientstatus";
	public static final String GETFROMQUEUE_PATH = SESSIONPATH + "/getfromqueue/{" + MEDICALRECORDID + "}";
	public static final String REMOVEFROMSESSION_PATH = SESSIONPATH + "/deletefromlist";
	public static final String INSERTINSESSION_PATH = SESSIONPATH + "/insertinsession/{" + POSITION + "}";

	public static final String STOPSESSION_PATH = SESSIONPATH + "/stopsession";
	
	
	// Patients and Users, the statusmessage contains the kind of user
	@GET(CHECKUSER_PATH)
	public StatusMessage checkUser();

	
	// Admin only; Getting the complete list with subscribed patients
	@GET(IMPATIENT_SVC_PATH)
	public Collection<Patient> getPatientList();

	// Admin only; Creating a new patient.
	@POST(NEWPATIENT_PATH)
	public Patient addNewPatient(@Body Patient patient);

	// Admin only; Editing a patient.
	@POST(EDITPATIENT_PATH)
	public Patient editPatient(@Body Patient patient);
	
	//Admin only; Getting a patient from the repository by medicalRecordId
	@GET(MEDICALRECORDID_SEARCH)
	public Patient findByMedicalRecordId(@Path(MEDICALRECORDID) String medicalrecordid);
	
	//Getting a Patient from that is currently in the waiting queue
	@GET(GETFROMQUEUE_PATH)
	public Patient getFromQueue(@Path(MEDICALRECORDID) String medicalrecordid);
	
	
	//Admin only; Getting the session list for today and initialise ;
	@GET(SESSIONPATH)
	public Collection<Patient> startSession();
	
	@GET(POLLSESSION_PATH)
	public Collection<Patient> pollSession();
	
	
	//STopping the session
	@POST(STOPSESSION_PATH)
	public StatusMessage stopSession();
	
	
	
	@GET(SESSIONPATH)
	public Collection<Patient> getSessionList(@Query(LOWERBOUND) String lowerbound, @Query(UPPERBOUND) String upperbound);

	@POST(MOVEUP_PATH)
	public Collection<Patient> moveUp(@Body Patient patient);
	
	@POST(MOVEDOWN_PATH)
	public Collection<Patient> moveDown(@Body Patient patient);
	
	@POST(CHANGESTATUS_PATH)
	public Collection<Patient> changeStatus(@Body Patient patient);
	
	@POST(REMOVEFROMSESSION_PATH)
	public Collection<Patient> deleteFromSession(@Body Patient patient);
	
	@POST(INSERTINSESSION_PATH)
	public Collection<Patient> insertInSession(@Body Patient patient, @Path(POSITION) int position);
	
	@POST(CHANGE_SETTINGS_PATH)
	public StatusMessage changeSettings(@Path(MACHINES) int howMuchMachines,@Path(TREATMENT_TIME) int treatmentTime);
	
	
	

	
}
