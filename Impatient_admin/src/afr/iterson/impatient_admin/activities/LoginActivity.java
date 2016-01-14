package afr.iterson.impatient_admin.activities;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.services.LoginService;
import afr.iterson.impatient_admin.utils.Constants;
import afr.iterson.impatient_admin.utils.LifecycleLoggingActivity;
import afr.iterson.impatient_admin.utils.Utils;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
//		if (prefs.getString(VideoServiceProxy.ACCESS_TOKEN, null) != null ||
//		!(prefs.getString(VideoServiceProxy.ACCESS_TOKEN, null).equals("")))
//		{
//			goToListActivity();
//		} else {
			mUsernameView = (EditText) findViewById(R.id.username);
			mServerUrl = (EditText) findViewById(R.id.serverurl);
			mPasswordView = (EditText) findViewById(R.id.password);
			mPasswordView
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView textView,
								int id, KeyEvent keyEvent) {
							if (id == R.id.login || id == EditorInfo.IME_NULL) {
								attemptLogin();
								return true;
							}
							return false;
						}
					});
			
			
			
			Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
			mSignInButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptLogin();
				}
			});
			mReceiver = new LoginReceiver();
		}

	//}

	private class LoginReceiver extends BroadcastReceiver
	{
		/**
		 * Hook method that's dispatched when the
		 * Login method returns.
		 */
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(TAG, "broadcastReceived...");
			if(intent.getIntExtra(LoginService.LOGIN_RESULTCODE, 0) == LoginService.RESULT_OK)
			{
				Intent intent2 = SessionViewActivity.makeIntent(getBaseContext());
				intent2.putParcelableArrayListExtra(LoginService.EXTRASESSIONLIST, 
						intent.getParcelableArrayListExtra(LoginService.EXTRASESSIONLIST));
				startActivity(intent2);
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				//overridePendingTransition(R.anim.activity_push_up_in, R.anim.activity_push_up_out);
				finish();
			}
			else
			{
				Utils.showToast(getBaseContext(), "Login Failed, please check password or username");
				mUsernameView.requestFocus();
			}

		}
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
	
	public void attemptLogin() 
	{
		String username = mUsernameView.getText().toString();
		String password = mPasswordView.getText().toString();
		String serverurl = mServerUrl.getText().toString();
		startService(LoginService.makeIntent(getApplicationContext(), username, password, serverurl));
	}

	public static Intent makeIntent(Context context) {
		return new Intent(context,LoginActivity.class);
	}

}
