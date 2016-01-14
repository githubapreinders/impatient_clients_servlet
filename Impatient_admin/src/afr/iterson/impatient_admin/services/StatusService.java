package afr.iterson.impatient_admin.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit.RetrofitError;
import afr.iterson.impatient_admin.activities.ImpatientApplication;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.model.StatusMessage;
import afr.iterson.impatient_admin.provider.DataMediator;
import afr.iterson.impatient_admin.retrofit.AdminSvc;
import afr.iterson.impatient_admin.retrofit.AdminSvcApi;
import afr.iterson.impatient_admin.retrofit.SecuredRestException;
import afr.iterson.impatient_admin.utils.Utils;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * This service handles most of the applications server requests. * Intents that 
 * are handled by this service have their own request id so that we know which api method to use;
 * Subsequently they have a identifier to send back a broadcast with the result. This service
 * contains many static values that are also accessed by other classes.
 */
public class StatusService extends IntentService
{
	private final static String TAG = StatusService.class.getSimpleName();
	public final static String REQUESTCODE = "statusservice_requestcode";
	public final static String RESULTCODE = "statusservice_resultcode";
	public final static int RESULT_OK = 1;
	public final static int RESULT_BAD = 0;
	
	
	
	public final static String KEY_PATIENT = "patient";
	public final static String KEY_PATIENTS_LIST = "patientslist";
	public final static String KEY_MRID = "medicalrecordid";
	public final static String KEY_HOWMUCHMACHINES = "howmuchmachines";
	public final static String KEY_TREATMENTTIME = "treatmenttime";
	public final static String KEY_POSITION = "position";
	
	
	
	public final static int REQ_NEWPATIENT = 1;
	public final static int REQ_EDITPATIENT = 2;
	public final static int REQ_FINDBYMRID = 3;
	public final static int REQ_GETFROMQUEUE = 4;
	public final static int REQ_STARTSESSION = 5;
	public final static int REQ_STOPSESSION = 6;
	public final static int REQ_MOVEUP = 7;
	public final static int REQ_MOVEDOWN = 8;
	public final static int REQ_CHANGESTATUS = 9;
	public final static int REQ_DELETEFROMSESSION = 10;
	public final static int REQ_INSERTINSESSION = 11;
	public final static int REQ_CHANGESETTINGS = 12;
	public final static int REQ_ALLPATIENTS = 13;
	public final static int REQ_LOGOUT = 14;
	
	
	public final static String NEWPATIENT = "newpatient";
	public final static String EDITPATIENT = "editpatient";
	public final static String FINDBYMRID = "findbymrid";
	public final static String GETFROMQUEUE = "getfromqueue";
	public final static String STARTSESSION = "startsession";
	public final static String STOPSESSION = "stopsession";
	public final static String MOVEUP = "moveup";
	public final static String MOVEDOWN = "movedown";
	public final static String CHANGESTATUS = "changestatus";
	public final static String DELETEFROMSESSION = "deletefromsession";
	public final static String INSERTINSESSION = "insertinsession";
	public final static String CHANGESETTINGS = "changesettings";
	public final static String ALLPATIENTS = "allpatients";
	public final static String KEY_STATUSMESSAGE = "statusmessage";
	public final static String KEY_LOGOUT = "logout";
	
	private AdminSvcApi api;
	
	public StatusService()
	{
		super("ImpatientAdminStatusService");
	}

	public StatusService(String name)
	{
		super("ImpatientAdminStatusService");
	}

	public static Intent makeIntent(Context context, int requestcode)
	{
		return new Intent(context, StatusService.class).putExtra(REQUESTCODE, requestcode);
	}

	
	/**
	 * All calls to the server are sorted by requestcode. When necessary a broadcast will be sent
	 * back to the activity to update the views.	
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		AdminSvcApi api = ((ImpatientApplication)getApplication()).getApi();
		if(api == null)
		{
			api = AdminSvc.getOrShowLogin(getApplicationContext());
		}
		try
		{
		int requestcode = intent.getIntExtra(REQUESTCODE, 0);	
		Log.d(TAG, "Making a proxy connection to the service...requestcode: " + requestcode);
		switch(requestcode)
		{
		case REQ_NEWPATIENT : 
		{
			Patient p = intent.getParcelableExtra(KEY_PATIENT);
			Patient p2 = api.addNewPatient(p);
			Intent i = makeIntentAndCheckForNull(EDITPATIENT, p2);
			i.putExtra(KEY_PATIENT, p2);
			sendABroadcast(i);
			break;
		}
		case REQ_EDITPATIENT : 
		{
			Patient p = intent.getParcelableExtra(KEY_PATIENT);
			Patient p2 = api.editPatient(p);
			Intent i = makeIntentAndCheckForNull(EDITPATIENT, p2);
			i.putExtra(KEY_PATIENT, p2);
			sendABroadcast(i);
			break;
		}
		case REQ_FINDBYMRID : 
		{
			String medicalrecordid = intent.getStringExtra(KEY_MRID);
			Patient p = api.findByMedicalRecordId(medicalrecordid);
			Intent i = makeIntentAndCheckForNull(FINDBYMRID , p);
			i.putExtra(KEY_PATIENT, p);
			sendABroadcast(i);
			break;
		}
		case REQ_GETFROMQUEUE : 
		{
			String medicalrecordid = intent.getStringExtra(KEY_MRID);
			Patient p = api.getFromQueue(medicalrecordid);
			Intent i = makeIntentAndCheckForNull(GETFROMQUEUE , p);
			i.putExtra(KEY_PATIENT, p);
			sendABroadcast(i);
			break;
		}
		case REQ_STARTSESSION : 
		{
			Collection<Patient> patients = api.startSession();
			Intent i = makeIntentAndCheckForNull(STARTSESSION, patients);
			i.putParcelableArrayListExtra(KEY_PATIENTS_LIST, new ArrayList<Patient>(patients));
			sendABroadcast(i);
			break;
		}
		case REQ_STOPSESSION : 
		{
			StatusMessage message = api.stopSession();
			Intent i = makeIntentAndCheckForNull(STOPSESSION, message);
			i.putExtra(KEY_STATUSMESSAGE, message.toString());
			sendABroadcast(i);
			break;
		}
		case REQ_MOVEUP : 
		{
			Patient p = intent.getParcelableExtra(KEY_PATIENT);
			Collection<Patient> patients = api.moveUp(p);	
			Intent i = makeIntentAndCheckForNull(MOVEUP, patients);
			sendBroadcast(i);
			break;
		}
		case REQ_MOVEDOWN : 
		{
			Patient p = intent.getParcelableExtra(KEY_PATIENT);
			Collection<Patient> patients = api.moveDown(p);	
			Intent i = makeIntentAndCheckForNull(MOVEDOWN, patients);
			sendBroadcast(i);
			break;
		}
		case REQ_CHANGESTATUS : 
		{
			Patient p = intent.getParcelableExtra(KEY_PATIENT);
			Collection<Patient> patients = api.changeStatus(p);	
			Intent i = makeIntentAndCheckForNull(CHANGESTATUS, patients);
			sendBroadcast(i);
			break;
		}
		case REQ_DELETEFROMSESSION : 
		{
			Patient p = intent.getParcelableExtra(KEY_PATIENT);
			Collection<Patient> patients = api.deleteFromSession(p);	
			Intent i = makeIntentAndCheckForNull(DELETEFROMSESSION, patients);
			sendBroadcast(i);
			break;
		}
		case REQ_INSERTINSESSION : 
		{
			Patient p = intent.getParcelableExtra(KEY_PATIENT);
			int position = intent.getIntExtra(KEY_POSITION, -1);
			Collection<Patient> patients = api.insertInSession(p, position);
			Intent i = makeIntentAndCheckForNull(INSERTINSESSION, patients);
			sendBroadcast(i);
			break;
		}
		case REQ_CHANGESETTINGS : 
		{
			int machines = intent.getIntExtra(KEY_HOWMUCHMACHINES, 0);
			int treatmenttime = intent.getIntExtra(KEY_TREATMENTTIME, 0);
			StatusMessage message = api.changeSettings(machines, treatmenttime);
			Intent intentchangesettings = makeIntentAndCheckForNull(CHANGESETTINGS,"something");
			sendABroadcast(intentchangesettings);
			break;
		}
		case REQ_ALLPATIENTS : 
		{
			Collection<Patient> allpatients = api.getPatientList();
			DataMediator mediator = new DataMediator(getApplicationContext());
			mediator.deletePatientsList();
			mediator.addPatientListToContentProvider(allpatients);
			Intent i  = makeIntentAndCheckForNull(ALLPATIENTS, allpatients);
			i.putParcelableArrayListExtra(KEY_PATIENTS_LIST, new ArrayList<Patient>(allpatients));
			sendBroadcast(i);
			break;
		}
		case REQ_LOGOUT:
		{
			DataMediator mediator = new DataMediator(getApplicationContext());
			mediator.deletePatientsList();
			break;
		}
		}
		} catch (SecuredRestException e)
		{
			e.printStackTrace();
		} catch (RetrofitError e)
		{
			e.printStackTrace();
		}

	}

	private void sendABroadcast(Intent intent)
	{
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	/**
	 *Convenience method to prepare a broadcast that avoids code repetition. 
	 */
	private Intent makeIntentAndCheckForNull( String intentname, Object object)
	{
		Intent intent = new Intent(intentname);
		
		if(object == null)
		{
			Log.d(TAG, "object is found to be null...");
			intent.putExtra(RESULTCODE, RESULT_BAD);
		}
		else
		{
			Log.d(TAG, "object is okay, declaring the result to be ok...");
			intent.putExtra(RESULTCODE, RESULT_OK);
		}
		return intent;
	}
	

}
