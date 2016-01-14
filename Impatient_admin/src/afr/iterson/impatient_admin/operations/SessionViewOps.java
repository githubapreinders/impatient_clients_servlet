package afr.iterson.impatient_admin.operations;

import java.lang.ref.WeakReference;
import java.util.List;

import afr.iterson.impatient_admin.activities.SessionViewActivity;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.aidl.PatientListRequest;
import afr.iterson.impatient_admin.aidl.PatientListResults;
import afr.iterson.impatient_admin.services.PollingService;
import afr.iterson.impatient_admin.utils.ConfigurableOps;
import afr.iterson.impatient_admin.utils.ContextView;
import afr.iterson.impatient_admin.utils.GenericServiceConnection;
import afr.iterson.impatient_admin.utils.GenericServiceConnection.GenericServiceCallBack;
import afr.iterson.impatient_admin.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Provides all the Video-related operations. It implements ConfigurableOps so
 * it can be created/managed by the GenericActivity framework. It extends
 * GenericAsyncTaskOps so its doInBackground() method runs in a background task.
 * It plays the role of the "Abstraction" in Bridge pattern and the role of the
 * "Presenter" in the Model-View-Presenter pattern.
 */
public class SessionViewOps implements ConfigurableOps<SessionViewOps.View>, GenericServiceCallBack

{
	/**
	 * Debugging tag used by the Android logger.
	 */
	private static final String TAG = SessionViewOps.class.getSimpleName();

	private WeakReference<SessionViewActivity> mActivity;

	/**
	 * This interface defines the minimum interface needed by the VideoOps class
	 * in the "Presenter" layer to interact with the SessionViewListActivity in
	 * the "View" layer.
	 */
	public interface View extends ContextView
	{
		/**
		 * Finishes the Activity the VideoOps is associated with.
		 */
		void finish();

		/**
		 * Sets the Adapter that contains List of Videos.
		 */
		void setAdapter(SessionListAdapter sessionAdapter);

	}

	
	/**
	 * Used to enable garbage collection.
	 */
	private WeakReference<SessionViewOps.View> mSessionView;

	/**
	 * The Adapter that is needed by ListView to show the list of Patients.
	 */
	private SessionListAdapter mAdapter;

	private GenericServiceConnection<PatientListRequest> connection;
	
	public GenericServiceCallBack  gscb = (GenericServiceCallBack) connection;

	private final Handler mDisplayHandler = new Handler();

	private final PatientListResults.Stub mPatientListResults = new PatientListResults.Stub()
	{

		@Override
		public void sendResults(List<afr.iterson.impatient_admin.aidl.Patient> results) throws RemoteException
		{
			displaySessionList(results);
		}
	};

	/**
	 * Default constructor that's needed by the GenericActivity framework.
	 */
	public SessionViewOps()
	{

	}

	/**
	 * Called after a runtime configuration change occurs to finish the
	 * initialisation steps.
	 */
	public void onConfiguration(SessionViewOps.View view, boolean firstTimeIn)
	{
		final String time = firstTimeIn ? "first time" : "second+ time";

		Log.d(TAG, "onConfiguration() called the " + time + " with view = " + view);

		mSessionView = new WeakReference<>(view);

		if (firstTimeIn)
		{
			mAdapter = new SessionListAdapter(mSessionView.get().getApplicationContext(), this);
			initializeNonViewFields();
			getSessionList();
		}
		mSessionView.get().setAdapter(mAdapter);
	}

	public void getSessionList()
	{
		Log.d(TAG, "making the call to the service");

		PatientListRequest request = connection.getInterface();

		if (request != null)
		{
			try
			{
				request.getPatientList(mPatientListResults);
			} catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void initializeNonViewFields()
	{
		connection = new GenericServiceConnection<PatientListRequest>(PatientListRequest.class, this);
	}

	public void bindService()
	{
		Log.d(TAG, "calling bindService()");

		// Launch the Service if they aren't already
		// running via a call to bindService(), which binds this
		// activity to the PollingService* if they aren't already
		// bound.

		if (connection.getInterface() == null)
			mSessionView
					.get()
					.getApplicationContext()
					.bindService(PollingService.makeIntent(mSessionView.get().getApplicationContext()), connection,
							Context.BIND_AUTO_CREATE);
	}

	/**
	 * Start a service that Uploads the Video having given Id. The service will
	 * make checks for a possible double upload and the size of the upload. When
	 * succesful The videoUri is saved for later use.
	 * 
	 * @param videoUri
	 */
	public void displaySessionList(final List<Patient> patients)
	{
			mDisplayHandler.post(new Runnable()
			{
				
				@Override
				public void run()
				{
					mAdapter.setSessionList(patients);
				}
			});
	}

	public WeakReference<SessionViewOps.View> getmSessionView()
	{
		return mSessionView;
	}

	
	@Override
	public void callBack()
	{
		Log.d(TAG, "making the first and only call to the Polling service via a callback from the generic connection");

		PatientListRequest request = connection.getInterface();

		if (request != null)
		{
			try
			{
				request.getPatientList(mPatientListResults);
			} catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void unbindPollingService()
	{
			if (connection.getInterface() != null)
			{
				Log.d(TAG, "disconnecting service");
				mSessionView.get().getApplicationContext().unbindService(connection);
			}
	}
	
	
	public GenericServiceConnection<PatientListRequest> getConnection()
	{
		return connection;
	}

	public SessionListAdapter getmAdapter()
	{
		return mAdapter;
	}

	

	
}
