package afr.iterson.impatient_admin.operations;

import java.lang.ref.WeakReference;
import java.util.List;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.provider.DataMediator;
import afr.iterson.impatient_admin.provider.PatientContract;
import afr.iterson.impatient_admin.services.StatusService;
import afr.iterson.impatient_admin.utils.ConfigurableOps;
import afr.iterson.impatient_admin.utils.ContextView;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class PatientListOps implements ConfigurableOps<PatientListOps.View>, LoaderManager.LoaderCallbacks<Cursor>
{
	private static String TAG = PatientListOps.class.getSimpleName();
	public PatientListCursorAdapter mAdapter;
	private WeakReference<PatientListOps.View> mPatientListView;
	private final Handler mDisplayHandler = new Handler();

	private final int[] viewids = { R.id.patient_name, R.id.patient_mrid, R.id.patient_app_time };
	public static final int LOADER_ID = 1;

	public interface View extends ContextView
	{
		public void finish();

		void setAdapter(PatientListCursorAdapter sessionAdapter);

		LoaderManager getViewLoaderManager();
	}

	@Override
	public void onConfiguration(View view, boolean firstTimeIn)
	{
		final String time = firstTimeIn ? "first time" : "second+ time";

		Log.d(TAG, "onConfiguration() called the " + time + " with view = " + view);

		mPatientListView = new WeakReference<>(view);

		if (firstTimeIn)
		{
			mPatientListView.get().getViewLoaderManager().initLoader(LOADER_ID, null, this);
			mAdapter = new PatientListCursorAdapter(mPatientListView.get().getApplicationContext(),
					R.layout.cardview_listitem_patientlist, null, DataMediator.getNeededColumns(), viewids);
		getPatientList();
		}
		mPatientListView.get().setAdapter(mAdapter);
	}

	/**
	 * Starts an intent service to retrieve the complete list of patients from
	 * the server; This service will do two things after that: A ContentResolver
	 * is created to fill the patients db, an the service will return in a
	 * BroadcastReceiver that is located in the PatientListActivity. From there
	 * on mAdapter will receive the list.
	 */
	public void getPatientList()
	{
		Context context = mPatientListView.get().getApplicationContext();
		Intent intent = StatusService.makeIntent(context, StatusService.REQ_ALLPATIENTS);
		context.startService(intent);
	}

	/**
	 * The loadermanager is connected to the ListView, The PatientCursorAdapter
	 * and the underlying ContentProvider. When it is finished loading it swaps
	 * the Cursor of the adapter after which the view will be refreshed.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		Loader<Cursor> loader = new CursorLoader(mPatientListView.get().getActivityContext(),
				PatientContract.PatientEntry.CONTENT_URI, DataMediator.getNeededColumns(), null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor)
	{
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		mAdapter.swapCursor(null);
	}

	public PatientListCursorAdapter getmAdapter()
	{
		return mAdapter;
	}

}
