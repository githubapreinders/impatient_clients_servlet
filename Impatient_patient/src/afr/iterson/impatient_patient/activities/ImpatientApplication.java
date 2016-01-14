package afr.iterson.impatient_patient.activities;

import afr.iterson.impatient_patient.retrofit.PatientSvcApi;
import android.app.Application;
import android.content.SharedPreferences;

public class ImpatientApplication extends Application
{
	public static PatientSvcApi api;
	
	public static SharedPreferences preferences;
	
	public PatientSvcApi getApi()
	{
		return null;
	}
}
