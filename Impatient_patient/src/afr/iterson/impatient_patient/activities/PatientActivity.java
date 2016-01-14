package afr.iterson.impatient_patient.activities;

import afr.iterson.impatient_patient.R;
import afr.iterson.impatient_patient.aidl.Patient;
import afr.iterson.impatient_patient.aidl.PatientRequest;
import afr.iterson.impatient_patient.aidl.PatientStatus;
import afr.iterson.impatient_patient.operations.PatientOps;
import afr.iterson.impatient_patient.operations.ViewStatus;
import afr.iterson.impatient_patient.services.LoginService;
import afr.iterson.impatient_patient.services.PollingService;
import afr.iterson.impatient_patient.services.StatusService;
import afr.iterson.impatient_patient.utils.Constants;
import afr.iterson.impatient_patient.utils.GenericActivity;
import afr.iterson.impatient_patient.utils.GenericServiceConnection;
import afr.iterson.impatient_patient.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class PatientActivity extends GenericActivity<PatientOps.View, PatientOps> implements PatientOps.View
{

	private static final int ANIMATION_TIME = 3000;
	public static final String REQUEST = "patientactivityrequest";
	private boolean flag;
	private boolean statusChanged = false;
	private TextView mHeader;
	private TextView mStatusMessage;
	private TextView mWaitingTime;
	private ScrollView mScrollview;
	private Button mCheckinCheckOutButton;
	private Button mPauseButton;
	private TextView mWaitingTimeText;
	private TextView mAppointmentTimeText;
	private TextView mAppointmentTime;
	private TextView mMinutes;

	public static Intent makeIntent(Context context, Patient patient)
	{
		Intent intent = new Intent(context, PatientActivity.class);
		intent.putExtra(LoginService.PATIENTINFO, patient);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.patient_activity);
		initializeViews();
		super.onCreate(savedInstanceState, PatientOps.class, this);
	}

	private void initializeViews()
	{
		flag = true;
		mHeader = (TextView) findViewById(R.id.textview1_headertext);
		mStatusMessage = (TextView) findViewById(R.id.textview2_statusmessage);
		mWaitingTimeText = (TextView) findViewById(R.id.textview3a_estwaitingtime);
		mWaitingTime = (TextView) findViewById(R.id.textview3b_itself);
		mMinutes = (TextView) findViewById(R.id.textview3c);
		mAppointmentTimeText = (TextView) findViewById(R.id.textview4a_apptime);
		mAppointmentTime = (TextView) findViewById(R.id.textview4b_itself);
		mCheckinCheckOutButton = (Button) findViewById(R.id.button_checkinorout);
		mPauseButton = (Button) findViewById(R.id.button_pause);
		mScrollview = (ScrollView) findViewById(R.id.scrollview1);
	}

	/**
	 * Methods to be called after the PatientOps is ready with its creation.
	 */
	@Override
	public void startupAnim()
	{
		mScrollview.setBackground(getOps().getmTransit());
		startupAnimation();
	}

	/**
	 * There are 5 different view states defined in the enumeratiion ViewStatus.
	 * The method first determines the Viewstatus investigating the patient
	 * object that was returned from the PollingService.
	 */
	@Override
	public void display()
	{
		Patient p = getOps().getCurrentPatient();
		setTitle("  " + p.getFirstName() + " " + p.getLastName());

		switch (getOps().getMvs().getStatus())
		{
		case no_appointment:
		{
			mHeader.setText(R.string.status_no_appointment);
			mStatusMessage.setText(R.string.message_no_appointment);
			mWaitingTimeText.setVisibility(View.INVISIBLE);
			mWaitingTime.setText("");
			mMinutes.setVisibility(View.INVISIBLE);
			mAppointmentTimeText.setText(R.string.appointment_date);
			if (p.getAppointmentDate() > 0)
			{
				mAppointmentTime.setText(Utils.convertTimeMillisToReadableFormat(p.getAppointmentDate()));
			} else
			{
				mAppointmentTime.setText("");
			}

			mCheckinCheckOutButton.setVisibility(View.INVISIBLE);
			mPauseButton.setVisibility(View.INVISIBLE);
			break;

		}
		case appointment_today:
		{
			mHeader.setText(R.string.status_checked_out);
			mStatusMessage.setText(R.string.message_checked_out);
			mWaitingTimeText.setVisibility(View.INVISIBLE);
			mWaitingTime.setText("");
			mMinutes.setVisibility(View.INVISIBLE);
			mAppointmentTimeText.setText(R.string.appointment_date);
			mAppointmentTime.setText(Utils.convertTimeMillisToReadableFormat(p.getAppointmentDate()));
			mCheckinCheckOutButton.setVisibility(View.VISIBLE);
			mCheckinCheckOutButton.setText(R.string.button_check_in);
			mPauseButton.setVisibility(View.INVISIBLE);
			break;
		}
		case checked_in:
		{
			mHeader.setText(R.string.status_checked_in);
			mStatusMessage.setText(R.string.message_checked_in);
			mWaitingTimeText.setVisibility(View.VISIBLE);
			mWaitingTime.setText(Utils.convertTimeMillisToReadableFormatMinutes(p.getWaitingTime()));
			mMinutes.setVisibility(View.VISIBLE);
			mAppointmentTimeText.setText(R.string.appointment_date);
			mAppointmentTime.setText(Utils.convertTimeMillisToReadableFormat(p.getAppointmentDate()));
			mCheckinCheckOutButton.setVisibility(View.VISIBLE);
			mCheckinCheckOutButton.setText(R.string.button_check_out);
			mPauseButton.setVisibility(View.VISIBLE);
			mPauseButton.setText(R.string.button_delay);
			break;
		}
		case temporary_unavailable:
		{
			mHeader.setText(R.string.status_temporary_unavailable);
			mStatusMessage.setText(R.string.message_temporary_unavailable);
			mWaitingTimeText.setVisibility(View.VISIBLE);
			mWaitingTime.setText(Utils.convertTimeMillisToReadableFormatMinutes(p.getWaitingTime()));
			mMinutes.setVisibility(View.VISIBLE);
			mAppointmentTimeText.setText(R.string.appointment_date);
			mAppointmentTime.setText(Utils.convertTimeMillisToReadableFormat(p.getAppointmentDate()));
			mCheckinCheckOutButton.setVisibility(View.VISIBLE);
			mCheckinCheckOutButton.setText(R.string.button_check_out);
			mPauseButton.setVisibility(View.VISIBLE);
			mPauseButton.setText(R.string.button_delayback);
			break;
		}
		case go_to_dressingroom:
			mHeader.setText(R.string.status_dressingroom);
			mStatusMessage.setText(R.string.message_dressingroom);
			mWaitingTimeText.setVisibility(View.INVISIBLE);
			mWaitingTime.setText("");
			mMinutes.setVisibility(View.INVISIBLE);
			mAppointmentTimeText.setText("");
			mAppointmentTime.setText(R.string.dont_forget);
			mCheckinCheckOutButton.setVisibility(View.INVISIBLE);
			mPauseButton.setVisibility(View.INVISIBLE);
			break;
		}

	}

	/**
	 * Handles clicks from the delay button.
	 */
	public void delayOrUndelay(View v)
	{
		Patient p = getOps().getCurrentPatient();
		if (p.getStatus() == PatientStatus.currently_unavailable)
		{
			Intent intent = StatusService.makeIntent(getActivityContext(), StatusService.REQ_CHANGESTATUS);
			p.setStatus(PatientStatus.available);
			getOps().setCurrentPatient(p);
			intent.putExtra(StatusService.KEY_PATIENT, p);
			startService(intent);
			getOps().displayPatient(p);
		} else
		{
			Intent intent = StatusService.makeIntent(getActivityContext(), StatusService.REQ_CHANGESTATUS);
			p.setStatus(PatientStatus.currently_unavailable);
			getOps().setCurrentPatient(p);
			intent.putExtra(StatusService.KEY_PATIENT, p);
			startService(intent);
			getOps().displayPatient(p);
		}
	}

	/**
	 * Handles clicks from the check in button.
	 */
	public void checkInOrOut(View v)
	{
		Patient p = getOps().getCurrentPatient();
		if (p.getStatus() == PatientStatus.available || p.getStatus() == PatientStatus.currently_unavailable)
		{
			Intent intent = StatusService.makeIntent(getActivityContext(), StatusService.REQ_CHANGESTATUS);
			p.setStatus(PatientStatus.unavailable);
			getOps().setCurrentPatient(p);
			intent.putExtra(StatusService.KEY_PATIENT, p);
			startService(intent);
			getOps().displayPatient(p);
		} else
		{
			Intent intent = StatusService.makeIntent(getActivityContext(), StatusService.REQ_CHANGESTATUS);
			p.setStatus(PatientStatus.available);
			getOps().setCurrentPatient(p);
			intent.putExtra(StatusService.KEY_PATIENT, p);
			startService(intent);
			getOps().displayPatient(p);
		}
	}

	/**
	 *Handles button click for the help screens 
	 */
	public void gotoHelp(View v)
	{
		startActivity(HelpActivity.makeIntent(getActivityContext()));
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
	}

	/**
	 * Handles the animation of the background border. The animation consists of
	 * two gradient drawables that fade in and out of each other. This process
	 * runs into a continuous loop that only is interrupted after the ViewStatus
	 * is changed. A new loop will be started then. To not block the UI this
	 * animation has to be put as a separate Runnable onto the message queue.
	 */
	void startupAnimation()
	{
		Handler hand = new Handler();
		hand.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				change();
			}

			private void change()
			{
				if (flag)
				{
					getOps().getmTransit().startTransition(ANIMATION_TIME);
					flag = false;
				} else
				{
					getOps().getmTransit().reverseTransition(ANIMATION_TIME);
					flag = true;
				}
				if (!isStatusChanged())
				{
					startupAnimation();
				} else
				{
					setStatusChanged(false);
				}
			}
		}, ANIMATION_TIME);

	}

	/**
	 * We don't want to have a service running an infinite loop being active
	 * when it is not necessary.
	 */
	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onStop()
	{
		if(!isChangingConfigurations())
		{
			Log.d(TAG, "Unbinding the polling service");
			getOps().unbindPollingService();
		}
		super.onStop();
	}
	
	
	
	/**
	 * Activate the polling service when the need arises.
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "Binding the polling service");
		getOps().initializeNonViewFields();
		getOps().bindService();
	}
	
	
	
	@Override
	public Intent getTheIntent()
	{
		return getIntent();
	}

	@Override
	public SharedPreferences getTheSharedPreferences()
	{
		return getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
	}

	/**
	 * The background scrollview has a background containing a
	 * transitiondrawable.This drawable resides in the PatientOps class to
	 * protect it against runtime changes. Depending on the status of the client
	 * the activity is given an animated border. The still running animation
	 * must be called out of its current loop so that the drawable can be
	 * replaced by an other one.
	 */
	public void update(ViewStatus status)
	{
		Log.d(TAG, "Status is updated");
		setStatusChanged(true);

		switch (status)
		{
		case appointment_today:
		{
			getOps().setmTransit((TransitionDrawable) getResources().getDrawable(R.drawable.transition));
			getOps().getmTransit().setCrossFadeEnabled(true);
			break;
		}
		case checked_in:
		{
			getOps().setmTransit((TransitionDrawable) getResources().getDrawable(R.drawable.transition2));
			getOps().getmTransit().setCrossFadeEnabled(true);
			break;
		}
		case go_to_dressingroom:
		{
			getOps().setmTransit((TransitionDrawable) getResources().getDrawable(R.drawable.transition3));
			getOps().getmTransit().setCrossFadeEnabled(true);
			break;
		}
		case no_appointment:
		{
			getOps().setmTransit((TransitionDrawable) getResources().getDrawable(R.drawable.transition4));
			getOps().getmTransit().setCrossFadeEnabled(true);
			break;
		}
		case temporary_unavailable:
		{
			getOps().setmTransit((TransitionDrawable) getResources().getDrawable(R.drawable.transition5));
			getOps().getmTransit().setCrossFadeEnabled(true);
			break;
		}
		default:
		{
			getOps().setmTransit((TransitionDrawable) getResources().getDrawable(R.drawable.transition));
			getOps().getmTransit().setCrossFadeEnabled(true);
			break;
		}

		}
		mScrollview.setBackground(getOps().getmTransit());
		startupAnimation();
	}

	public boolean isStatusChanged()
	{
		return statusChanged;
	}

	public void setStatusChanged(boolean statusChanged)
	{
		Log.d(TAG, "statusChanged is changed to " + statusChanged);
		this.statusChanged = statusChanged;
	}

	@Override
	public Activity getTheActivity()
	{
		return this;
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{

		case R.id.action_settings:
		{
			Intent intent = LoginActivity.makeIntent(getActivityContext());
			intent.putExtra(REQUEST, true);
			startActivity(intent);
			finish();
			break;
		}
		default:
			break;
		}
		return true;
	}

	

}
