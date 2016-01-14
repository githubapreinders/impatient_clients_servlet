package afr.iterson.impatient_admin.operations;

import java.lang.ref.WeakReference;

import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.services.StatusService;
import afr.iterson.impatient_admin.utils.ConfigurableOps;
import afr.iterson.impatient_admin.utils.ContextView;
import android.content.Intent;
import android.util.Log;

public class PatientOps implements ConfigurableOps<PatientOps.View>
{
private static final String TAG = PatientOps.class.getSimpleName();

	private WeakReference<PatientOps.View> mPatientOps;
	private Patient currentPatient;

	public interface View extends ContextView
	{
		public void finish();

		public void displayPatient(Patient patient);

		public Intent getTheIntent();
	}

	@Override
	public void onConfiguration(View view, boolean firstTimeIn)
	{
		mPatientOps = new WeakReference<>(view);
		if (firstTimeIn)
		{
			currentPatient = new Patient("","","","",0,"",0);
			Intent intent = mPatientOps.get().getTheIntent();
			if (intent != null && intent.hasExtra(StatusService.KEY_PATIENT))
			{
				Log.d(TAG, "setting current patient from parelable");
				currentPatient = intent.getParcelableExtra(StatusService.KEY_PATIENT);
			}
		}
	mPatientOps.get().displayPatient(currentPatient);
	}

	
	public Patient getCurrentPatient()
	{
		return currentPatient;
	}


	public void setCurrentPatient(Patient currentPatient)
	{
		this.currentPatient = currentPatient;
	}
	
	

}
