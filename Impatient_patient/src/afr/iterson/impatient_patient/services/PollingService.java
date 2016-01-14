package afr.iterson.impatient_patient.services;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import afr.iterson.impatient_patient.R;
import afr.iterson.impatient_patient.aidl.Patient;
import afr.iterson.impatient_patient.aidl.PatientRequest;
import afr.iterson.impatient_patient.aidl.PatientResults;
import afr.iterson.impatient_patient.aidl.PatientStatus;
import afr.iterson.impatient_patient.operations.NotificationReceiver;
import afr.iterson.impatient_patient.retrofit.PatientSvc;
import afr.iterson.impatient_patient.retrofit.PatientSvcApi;
import afr.iterson.impatient_patient.retrofit.SecuredRestException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class PollingService extends Service
{

	private final static String TAG = PollingService.class.getSimpleName();
	
	/**
	 * 
	 */
	private Thread thread;
	private long SLEEPTIME = 15000;

	
	public static Intent makeIntent(Context context)
	{
		return new Intent(context, PollingService.class);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return mPatientRequestImp;
	}

	private final PatientRequest.Stub mPatientRequestImp = new PatientRequest.Stub()
	{

		@Override
		public void getPatient(String medicalRecordId , PatientResults results) throws RemoteException
		{
			Log.d(TAG, "Getting patient in pollingservice... ");

			MyRunnable myrunnable = new MyRunnable(medicalRecordId, results);
			thread = new Thread(myrunnable);
			thread.start();
		}
		
	};

	
	
	
	
	class MyRunnable implements Runnable
	{
	private PatientSvcApi api;
	private PatientResults mResults;
	private String mMedicalRecordId;
	public boolean isRunning;
	
	public MyRunnable(String medicalRecordId,PatientResults results)
		{
			mResults = results;
			mMedicalRecordId = medicalRecordId;
			api = PatientSvc.getOrShowLogin(getApplicationContext());
		}
		
		@Override
		public void run()
		{
		//TODO CHANGE TO TRUE!	
		isRunning = true;
		int counter=0;
			do
			{
			Log.d(TAG, "Getting patient  "+String.valueOf(counter++) + " from the service");
			Patient p;
			try
			{
				p = api.getStatus(mMedicalRecordId);
				if(p.getStatus() == PatientStatus.under_treatment && p.isNotified() == false)
				{
					Log.d(TAG, "Notifying user to go to dressing room");
					p.setNotified(true);
					Intent intent = StatusService.makeIntent(getApplicationContext(), StatusService.REQ_SETNOTIFIED);
					intent.putExtra(StatusService.KEY_PATIENT, p);
					startService(intent);
					notifyUser();
				}
				List<Patient> results = new ArrayList<>();
				results.add(p);
				mResults.sendResults(results);
			} 
			
			catch (RemoteException e)
			{
				setRunning(false);
				e.printStackTrace();
			}
			catch (SecuredRestException e)
			{
				e.printStackTrace();
			} catch (RetrofitError e)
			{
				e.printStackTrace();
			}
			try
			{
				Thread.sleep(SLEEPTIME);
			} catch (InterruptedException e)
			{
				setRunning(false);
				e.printStackTrace();
			}
			}while(isRunning);
			Log.d(TAG, "thread exiting...");	
		}

		public boolean isRunning()
		{
			return isRunning;
		}

		public void setRunning(boolean isRunning)
		{
			this.isRunning = isRunning;
		}
		
	}

	
	public void notifyUser()
	{
		Intent intent = new Intent(this, NotificationReceiver.class);
		intent.setAction(StatusService.SETNOTIFIED);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		Notification n  = new Notification.Builder(this)
		        .setContentTitle("Your turn now!")
		        .setContentText("Please go to the dressing room")
		        .setSmallIcon(R.drawable.ic_schedule)
		        .setContentIntent(pIntent)
		        .setPriority(Notification.PRIORITY_HIGH)
		        .setAutoCancel(true)
		        .addAction(R.drawable.patient_waiting_50, "", pIntent)
		        .build();
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, n); 

	}
	
	@Override
	public void onDestroy()
	{
		Log.d(TAG, "interrupting the polling thread...");
		thread.interrupt();
		super.onDestroy();
	}

	
}
