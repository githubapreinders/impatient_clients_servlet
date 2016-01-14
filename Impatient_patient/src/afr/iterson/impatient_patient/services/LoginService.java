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

public class LoginService extends IntentService
{
	private final static String TAG = LoginService.class.getSimpleName();

	public static final String LOGIN_SERVICE_RESULT = "patient_app_login_service_result";
	public static final String LOGIN_RESULTCODE = "login_service_resultcode";
	public static final int RESULT_OK = 1;
	public static final int RESULT_BAD = 2;
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String SERVERURL = "serverurl";
	public static final String PATIENTINFO = "patientinfo";
	
	private  String mUsername;
	private  String mPassword;
	private  String mServerurl;
	private Context context;


	
	public LoginService()
	{
		super("ImpatientAdminLoginService");
	}

	
	public LoginService(String name)
	{
		super("ImpatientAdminLoginService");
	}

	
	
	
	public static Intent makeIntent(Context context, String username, String password, String serverurl)
	{
		return new Intent(context, LoginService.class).putExtra(USERNAME, username).putExtra(PASSWORD, password)
				.putExtra(SERVERURL, serverurl);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.d(TAG, "Making a proxy connection to the service...");

		mUsername = intent.getStringExtra(USERNAME);
		mPassword = intent.getStringExtra(PASSWORD);
		mServerurl = intent.getStringExtra(SERVERURL);
		final PatientSvcApi api = PatientSvc.init(mServerurl, mUsername, mPassword);
		ImpatientApplication.api = api;
		Patient patient = null;
		try
		{
			patient = PatientSvc.getOrShowLogin(context).getAppointmentDate(mUsername);

		} catch (SecuredRestException e)
		{
			e.printStackTrace();
		} catch (RetrofitError e)
		{
			e.printStackTrace();
		}
		sendBroadcast(patient);

	}

	private void sendBroadcast(Patient patient)
	{
		Log.d(TAG, "send Broadcast to LoginActivity...");
		Intent intent = new Intent(LOGIN_SERVICE_RESULT);
		if(patient!=null)
		{
			intent.putExtra(LOGIN_RESULTCODE, RESULT_OK);
			intent.putExtra(PATIENTINFO, patient);
			
		}
		else
		{
			intent.putExtra(LOGIN_RESULTCODE, RESULT_BAD);
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	
	
}
