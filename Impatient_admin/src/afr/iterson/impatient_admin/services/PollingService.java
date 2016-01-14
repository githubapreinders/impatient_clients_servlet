package afr.iterson.impatient_admin.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit.RetrofitError;
import afr.iterson.impatient_admin.activities.ImpatientApplication;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.aidl.PatientListRequest;
import afr.iterson.impatient_admin.aidl.PatientListResults;
import afr.iterson.impatient_admin.retrofit.AdminSvc;
import afr.iterson.impatient_admin.retrofit.AdminSvcApi;
import afr.iterson.impatient_admin.retrofit.SecuredRestException;
import afr.iterson.impatient_admin.utils.Utils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
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
		return mPatientListRequestImp;
	}

	private final PatientListRequest.Stub mPatientListRequestImp = new PatientListRequest.Stub()
	{
		@Override
		public void getPatientList(PatientListResults results) throws RemoteException
		{
			Log.d(TAG, "Getting patientlist in pollingservice... ");

			MyRunnable myrunnable = new MyRunnable(results);
			thread = new Thread(myrunnable);
			thread.start();
		}
	};

	class MyRunnable implements Runnable
	{
		private AdminSvcApi api;
		private PatientListResults mResults;
		public boolean isRunning;

		public MyRunnable(PatientListResults results)
		{
			mResults = results;
			api = AdminSvc.getOrShowLogin(getApplicationContext());
		}

		@Override
		public void run()
		{
			// TODO CHANGE TO TRUE!
			isRunning = true;
			int counter = 0;
			do
			{
				Log.d(TAG, "Getting patientlist " + String.valueOf(counter++) + " from the service");
				List<Patient> list;
				// for (int i = 0; i < 15; i++)
				// {
				// list.add(Utils.makeRandomPatient());
				// }
				try
				{
					list = new ArrayList<>(api.pollSession());
					mResults.sendResults(list);
				}

				catch (RemoteException e)
				{
					setRunning(false);
					e.printStackTrace();
				} catch (SecuredRestException e)
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
			} while (isRunning);
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

	@Override
	public void onDestroy()
	{
		Log.d(TAG, "interrupting the polling thread...");
		thread.interrupt();
		super.onDestroy();
	}
}
