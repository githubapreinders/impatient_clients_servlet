package afr.iterson.impatient_patient.operations;

import java.lang.ref.WeakReference;
import java.util.List;

import afr.iterson.impatient_patient.R;
import afr.iterson.impatient_patient.activities.PatientActivity;
import afr.iterson.impatient_patient.aidl.Patient;
import afr.iterson.impatient_patient.aidl.PatientRequest;
import afr.iterson.impatient_patient.aidl.PatientResults;
import afr.iterson.impatient_patient.aidl.PatientStatus;
import afr.iterson.impatient_patient.services.LoginService;
import afr.iterson.impatient_patient.services.PollingService;
import afr.iterson.impatient_patient.utils.ConfigurableOps;
import afr.iterson.impatient_patient.utils.Constants;
import afr.iterson.impatient_patient.utils.ContextView;
import afr.iterson.impatient_patient.utils.GenericServiceConnection;
import afr.iterson.impatient_patient.utils.GenericServiceConnection.GenericServiceCallBack;
import afr.iterson.impatient_patient.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class PatientOps implements ConfigurableOps<PatientOps.View>, GenericServiceCallBack,afr.iterson.impatient_patient.operations.ViewStatusObserver

{
	
	private static final String TAG = PatientOps.class.getSimpleName();

	/**
	 * Methods to communicate with the PatientActivity
	 */
	public interface View extends ContextView
	{
		void finish();

		void display();
		
		void update(ViewStatus status);
		
		void startupAnim();
		
		Activity getTheActivity();

		Intent getTheIntent();
		
		SharedPreferences getTheSharedPreferences();
		
	}

	/**
	 * Holds the current Patient object and adapts it when the Polling Service returns.
	 */
	private Patient currentPatient;
	
	/**
	 * Holds the current display status and listens to change via
	 * an Observer pattern.
	 */
	private MyViewStatus mvs;
	//
	private boolean firstentered;
	
	/**
	 * Indirect reference to the Patient Activity
	 */
	private WeakReference<PatientOps.View> mPatientView;
	
	/**
	 * Facilitates the background animation; since its shape is dependent
	 * on the ViewStatus it needs to be protected against configuration changes 
	 */
	private TransitionDrawable mTransit;


	/**
	 * connection that facilitates polling on the server to get
	 * waiting times and other changes.
	 */
	private GenericServiceConnection<PatientRequest> connection;

	
	/**
	 * The generic service gives a callback when the connection is established,
	 * this callback will trigger the polling service to go ahead.
	 */
	public GenericServiceCallBack gscb = (GenericServiceCallBack) connection;

	/**
	 * A Handler that facilitates updating the view. Necessary because its return value
	 * comes from another thread.
	 */
	private final Handler mDisplayHandler = new Handler();

	/**
	 * Implementation of an aidl interface; This one funcions as a callback hook
	 * from the Polling Service.
	 */
	private final PatientResults.Stub mPatientResults = new PatientResults.Stub()
	{
		@Override
		public void sendResults(List<Patient> results) throws RemoteException
		{
			if(results!=null && results.size()>0)
			{
				displayPatient(results.get(0));
			}
		}
	};

	/**
	 * Default constructor that's needed by the GenericActivity framework.
	 */
	public PatientOps()
	{

	}

	/**
	 * Called after a runtime configuration change occurs to finish the
	 * initialisation steps.
	 */
	public void onConfiguration(PatientOps.View view, boolean firstTimeIn)
	{
		final String time = firstTimeIn ? "first time" : "second+ time";

		Log.d(TAG, "onConfiguration() called the " + time + " with view = " + view);

		mPatientView = new WeakReference<>(view);

		if (firstTimeIn)
		{
			firstentered = true;
			currentPatient = new Patient();
			mvs = new MyViewStatus();
			mvs.registerObserver(this);
			if (mPatientView.get().getTheIntent() != null)
			{
				Log.d(TAG, "getting patient from parcelable");
				Intent intent = mPatientView.get().getTheIntent();
				if (intent.hasExtra(LoginService.PATIENTINFO))
				{
					currentPatient = intent.getParcelableExtra(LoginService.PATIENTINFO);
				}
			} else
			{
				Log.d(TAG, "getting patient from sharedprefs");
				SharedPreferences prefs = mPatientView.get().getTheSharedPreferences();
				currentPatient.setMedicalRecordId(prefs.getString(Constants.USERNAME, "default"));
			}
			mTransit = new TransitionDrawable(new Drawable[]{mPatientView.get().getTheActivity().getResources().getDrawable(R.drawable.background1),
					mPatientView.get().getTheActivity().getResources().getDrawable(R.drawable.background2)});
			mPatientView.get().startupAnim();
			initializeNonViewFields();
		}
		else
		{
		mPatientView.get().startupAnim();
		mPatientView.get().display();
		}
	}

	public void initializeNonViewFields()
	{
		connection = new GenericServiceConnection<PatientRequest>(PatientRequest.class, this);
	}

	public void bindService()
	{
		Log.d(TAG, "calling bindService()");
		if (connection.getInterface() == null)
			mPatientView
					.get()
					.getApplicationContext()
					.bindService(PollingService.makeIntent(mPatientView.get().getApplicationContext()), connection,
							Context.BIND_AUTO_CREATE);
	}

	
	public void unbindPollingService()
	{
		if (connection.getInterface() != null)
		{
			Log.d(TAG, "disconnecting service");
			mPatientView.get().getApplicationContext().unbindService(connection);
		}
	}
	
	
	
	
	/**
	 * Callback from the aidl service.
	 */
	public void displayPatient(final Patient patient)
	{
		ViewStatus status = determineViewStatus(patient);
		if(firstentered == true || null == mvs.getStatus() || mvs.getStatus() != status)
		{
			firstentered = false;
			mvs.setStatus(status);
		}
		
		mDisplayHandler.post(new Runnable()
		{

			@Override
			public void run()
			{
				currentPatient = patient;
				mPatientView.get().display();
			}
		});
	}

	public WeakReference<PatientOps.View> getmSessionView()
	{
		return mPatientView;
	}

	@Override
	public void callBack()
	{
		Log.d(TAG, "making the first and only call to the Polling service via a callback from the generic connection");
		PatientRequest request = connection.getInterface();
		if (request != null)
		{
			try
			{
				request.getPatient(currentPatient.getMedicalRecordId(), mPatientResults);
			} catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}
	}

	

	
	/**
	 *Investigates the returned Patient object to see which fields and animations
	 *must be displayed.  
	 */
	public ViewStatus determineViewStatus(Patient p)
	{
		long apptime = p.getAppointmentDate();
		if(!( Utils.inTodaysBoundaries(apptime)) && p.getStatus() == PatientStatus.unavailable)
		{
			Log.d(TAG, "status no_appointment returned");
			return ViewStatus.no_appointment;
		}
		if(( Utils.inTodaysBoundaries(apptime)) && p.getStatus() == PatientStatus.unavailable)
		{
			Log.d(TAG, "status appointment today returned");
			return ViewStatus.appointment_today;
		}
		if(p.getStatus() == PatientStatus.available)
		{
			Log.d(TAG, "status checked in returned");
			return ViewStatus.checked_in;
		}
		if(p.getStatus() == PatientStatus.currently_unavailable)
		{
			Log.d(TAG, "status temp unavailable returned");
			return ViewStatus.temporary_unavailable;
		}
		if(p.getStatus() == PatientStatus.under_treatment || p.getStatus() == PatientStatus.available && p.getWaitingTime()<0)
		{
			Log.d(TAG, "status go to dressingroom returned");
			return ViewStatus.go_to_dressingroom;
		}
		
		
		
		Log.d(TAG, "default status returned");
		return ViewStatus.checked_in;
	}
	
	
	
	
	public GenericServiceConnection<PatientRequest> getConnection()
	{
		return connection;
	}

	public Patient getCurrentPatient()
	{
		return currentPatient;
	}

	public MyViewStatus getMvs()
	{
		return mvs;
	}

	public void setMvs(MyViewStatus mvs)
	{
		this.mvs = mvs;
	}

	/**
	 * Callback from the ViewStatus object to indicate that the value has changed and that the animation in the activity
	 * has to be updated.
	 */
	@Override
	public void update(final ViewStatus status)
	{
		mDisplayHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				Log.d(TAG, "updating transition drawable");
				mPatientView.get().update(status);
			}
		});
	}

	/**
	 * hooks from the activity to get the Animation that has to be displayed. 
	 */
	public TransitionDrawable getmTransit()
	{
		return mTransit;
	}

	public void setmTransit(TransitionDrawable mTransit)
	{
		this.mTransit = mTransit;
	}

	public void setCurrentPatient(Patient currentPatient)
	{
		this.currentPatient = currentPatient;
	}

	public void setConnection(GenericServiceConnection<PatientRequest> connection)
	{
		this.connection = connection;
	}
}
