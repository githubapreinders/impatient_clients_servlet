package afr.iterson.impatient_patient.services;

import retrofit.RetrofitError;
import afr.iterson.impatient_patient.activities.ImpatientApplication;
import afr.iterson.impatient_patient.aidl.Patient;
import afr.iterson.impatient_patient.retrofit.PatientSvc;
import afr.iterson.impatient_patient.retrofit.PatientSvcApi;
import afr.iterson.impatient_patient.retrofit.SecuredRestException;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Intents that are handled by this service have their own request id so
 * that we know which api method to use; Subsequently they have a identifier
 * to send back a broadcast with the result. This service contains many static
 * values that are used in intents and broadcastreceivers.
 */
public class StatusService extends IntentService
{

	private final static String TAG = StatusService.class.getSimpleName();
	public final static String REQUESTCODE = "statusservice_requestcode";
	public final static String RESULTCODE = "statusservice_resultcode";
	public final static int RESULT_OK = 1;
	public final static int RESULT_BAD = 0;

	public final static String KEY_PATIENT = "patient";
	public final static String KEY_MRID = "medicalrecordid";
	public final static String KEY_POSITION = "position";
	public final static String KEY_LOGOUT = "logout";

	
	public final static int REQ_CHANGESTATUS = 1;
	public final static int REQ_GETSTATUS = 2;
	public final static int REQ_GETAPPOINTMENTDATE = 3;
	public final static int REQ_LOGOUT = 4;
	public final static int REQ_SETNOTIFIED = 5;

	public final static String CHANGESTATUS = "changestatus";
	public final static String GETAPPDATE = "getappdate";
	public final static String SETNOTIFIED = "afr.iterson.setnotified";

	private PatientSvcApi api;

	public StatusService()
	{
		super("ImpatientAdminStatusService");
	}

	public StatusService(String name)
	{
		super("ImpatientAdminStatusService");
	}

	// The caller of the service must make clear which api method he wants to
	// access.
	public static Intent makeIntent(Context context, int requestcode)
	{
		return new Intent(context, StatusService.class).putExtra(REQUESTCODE, requestcode);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.d(TAG, "handling intent in statusservice...");
		api = PatientSvc.getOrShowLogin(getApplicationContext());
		try
		{
			int requestcode = intent.getIntExtra(REQUESTCODE, 0);
			Log.d(TAG, "Making a proxy connection to the service...requestcode: " + requestcode);
			switch (requestcode)
			{
			case REQ_CHANGESTATUS:
			{
				Log.d(TAG, "changing status...");
				Patient p = intent.getParcelableExtra(KEY_PATIENT);
				Patient p2 = api.changePatientStatus(p);
				Intent i = makeIntentAndCheckForNull(CHANGESTATUS, p2);
				i.putExtra(KEY_PATIENT, p2);
				sendABroadcast(i);
				break;
			}
			case REQ_GETAPPOINTMENTDATE:
			{
				Log.d(TAG, "getting appointment time...");
				String s = intent.getStringExtra(KEY_MRID);
				Patient p = api.getAppointmentDate(s);
				Intent i = makeIntentAndCheckForNull(GETAPPDATE, p);
				i.putExtra(KEY_PATIENT, p);
				sendABroadcast(i);
				break;
			}
			case REQ_SETNOTIFIED:
			{
				Log.d(TAG, "telling server that notification is sent ...");
				Patient p = intent.getParcelableExtra(KEY_PATIENT);
				Patient p2 = api.changePatientStatus(p);
				break;
			}
			case REQ_LOGOUT:
			{
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

	private Intent makeIntentAndCheckForNull(String intentname, Object object)
	{

		Intent intent = new Intent(intentname);

		if (object == null)
		{
			Log.d(TAG, "object is found to be null...");
			intent.putExtra(RESULTCODE, RESULT_BAD);
		} else
		{
			Log.d(TAG, "object is okay, declaring the result to be ok...");
			intent.putExtra(RESULTCODE, RESULT_OK);
		}
		return intent;

	}

}
