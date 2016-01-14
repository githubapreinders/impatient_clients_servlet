package afr.iterson.impatient_patient.activities;

import afr.iterson.impatient_patient.R;
import afr.iterson.impatient_patient.aidl.Patient;
import afr.iterson.impatient_patient.services.LoginService;
import afr.iterson.impatient_patient.utils.Constants;
import afr.iterson.impatient_patient.utils.LifecycleLoggingActivity;
import afr.iterson.impatient_patient.utils.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends LifecycleLoggingActivity
{

	private final static String TAG = LoginActivity.class.getSimpleName();

	private EditText mUsernameView;
	private EditText mPasswordView;
	private EditText mServerUrl;
	private LoginReceiver mReceiver;
	SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mReceiver = new LoginReceiver();
		setContentView(R.layout.activity_login);
		mServerUrl = (EditText) findViewById(R.id.serverurl);
		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == R.id.login || id == EditorInfo.IME_NULL)
				{
					attemptLogin();
					return true;
				}
				return false;
			}
		});
		prefs = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		if (prefs.contains(Constants.USERNAME) && prefs.contains(Constants.PASSWORD))
		{
			Intent intent = getIntent();
			if(intent.hasExtra(PatientActivity.REQUEST))
			{
				
			}
			else
			{
				attemptLogin();
			}
		}
		
		
		Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				attemptLogin();
			}
		});
	}

	private class LoginReceiver extends BroadcastReceiver
	{
		/**
		 * Hook method that's dispatched when the Login method returns.
		 */
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(TAG, "broadcastReceived...");
			if (intent.getIntExtra(LoginService.LOGIN_RESULTCODE, 0) == LoginService.RESULT_OK)
			{
				if(prefs.getString(Constants.USERNAME, null) == null && prefs.getString(Constants.PASSWORD, null) == null)
				{
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(Constants.USERNAME, mUsernameView.getText().toString());
					editor.putString(Constants.PASSWORD, mPasswordView.getText().toString());
					editor.commit();
				}
				Intent intent2 = PatientActivity.makeIntent(getBaseContext(),
						(Patient) intent.getParcelableExtra(LoginService.PATIENTINFO));
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intent2);
				finish();

			} else
			{
				Utils.showToast(getBaseContext(), "Login Failed, please check password or username");
				mUsernameView.requestFocus();
			}

		}
	}

	public void gotoHelp(View v)
	{
		startActivity(HelpActivity.makeIntent(getApplicationContext()));
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
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
		IntentFilter intentFilter = new IntentFilter(LoginService.LOGIN_SERVICE_RESULT);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		// Register the BroadcastReceiver.
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
	}

	/**
	 * Hook method that gives a final chance to release resources and stop
	 * spawned threads. onDestroy() may not always be called-when system kills
	 * hosting process
	 */
	@Override
	protected void onPause()
	{
		super.onPause();

		// Unregister BroadcastReceiver.
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
	}

	//Starting an Intent Service to connect to the server; this will return to the broadcastreceiver.
	public void attemptLogin()
	{
		if(prefs.getString(Constants.USERNAME, null) != null && prefs.getString(Constants.PASSWORD, null)!=null)
		{
			startService(LoginService.makeIntent(getApplicationContext(), 
					prefs.getString(Constants.USERNAME, null), 
					prefs.getString(Constants.PASSWORD, null), 
					mServerUrl.getText().toString()));
		}
		else
		{
		startService(LoginService.makeIntent(getApplicationContext(), 
				mUsernameView.getText().toString(), 
				mPasswordView.getText().toString(), 
				mServerUrl.getText().toString()));
		}
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, LoginActivity.class);
	}

}
