package afr.iterson.impatient_admin.activities;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.model.AdminSettings;
import afr.iterson.impatient_admin.operations.SettingsOps;
import afr.iterson.impatient_admin.services.StatusService;
import afr.iterson.impatient_admin.utils.GenericActivity;
import afr.iterson.impatient_admin.utils.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class SettingsActivity extends GenericActivity<SettingsOps.View, SettingsOps> implements SettingsOps.View
{

	public static final String TAG = SettingsActivity.class.getSimpleName();

	Button mConfirmButton;
	ImageView mHelpButton;
	Spinner mSpinnerTreatmentTime;
	Spinner mSpinnerMachines;
	SettingsReceiver mReceiver;

	private class SettingsReceiver extends BroadcastReceiver
	{
		/**
		 * Hook method that's dispatched when the Settings are changed.
		 */
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(TAG, "broadcastReceived...");
			if (intent.getIntExtra(StatusService.RESULTCODE, 0) == StatusService.RESULT_OK)
			{
				Utils.showToast(getBaseContext(), "settings changed succesfully...");
			} else
			{
				Utils.showToast(getBaseContext(), "settings not changed...");
			}
		}
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, SettingsActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_settings);
		initializeViewFields();
		mReceiver = new SettingsReceiver();
		super.onCreate(savedInstanceState, SettingsOps.class, this);
	}

	public void initializeViewFields()
	{
		mSpinnerMachines = (Spinner)findViewById(R.id.spinner_machines);
		mSpinnerMachines.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		mSpinnerTreatmentTime = (Spinner)findViewById(R.id.spinner_treatmenttime);
		mSpinnerTreatmentTime.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		mConfirmButton = (Button)findViewById(R.id.button1);
		mConfirmButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = StatusService.makeIntent(getActivityContext(), StatusService.REQ_CHANGESETTINGS);
				intent.putExtra(StatusService.KEY_HOWMUCHMACHINES, getOps().getCurrentSettings().amountOfMachines);
				intent.putExtra(StatusService.KEY_TREATMENTTIME, getOps().getCurrentSettings().MINUTES);
				startService(intent);
			}
		});
		mHelpButton = (ImageView)findViewById(R.id.helpbutton);
		mHelpButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = HelpActivity.makeIntent(getApplicationContext());
				startActivity(intent);
			}
		});
	}

	class CustomOnItemSelectedListener implements OnItemSelectedListener
	{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			String item1 =(String)mSpinnerMachines.getSelectedItem();
			String item2 =(String)mSpinnerTreatmentTime.getSelectedItem();
			getOps().setCurrentSettings(new AdminSettings(Integer.parseInt(item1),Integer.parseInt(item2)));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0)
		{
			
		}
		
	}
	
	public void displaySettings(AdminSettings settings)
	{
		if (settings != null)
		{
			Log.d(TAG, "displaying known data");
			mSpinnerMachines.setSelection(settings.getAmountOfMachines()-1);
			mSpinnerTreatmentTime.setSelection(settings.getMINUTES()-1);
		} else
		{
			Log.d(TAG, "Settings could not be displayed");
		}
	}

	
	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Register BroadcastReceiver that receives result from LoginService
		registerReceiver();
	}

	private void registerReceiver()
	{
		// Create an Intent filter that handles Intents from the
		// LoginService.
		IntentFilter intentFilter = new IntentFilter(StatusService.CHANGESETTINGS);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		// Register the BroadcastReceiver.
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
	}

}
