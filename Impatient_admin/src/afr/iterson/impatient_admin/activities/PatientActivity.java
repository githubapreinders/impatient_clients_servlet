package afr.iterson.impatient_admin.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.activities.PatientListActivity.MenuClicker;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.operations.PatientOps;
import afr.iterson.impatient_admin.services.LoginService;
import afr.iterson.impatient_admin.services.StatusService;
import afr.iterson.impatient_admin.utils.GenericActivity;
import afr.iterson.impatient_admin.utils.Utils;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

public class PatientActivity extends GenericActivity<PatientOps.View, PatientOps> implements PatientOps.View
{

	public static final String TAG = PatientActivity.class.getSimpleName();

	ImageView mOkaybutton;
	ImageView mNewpatientbutton;
	ImageView mMenudotsbutton;
	EditText mFirstname;
	EditText mLastname;
	TextView mAppointmentdate;
	TextView mBirthdate;
	TextView mMedicalRecordId;
	TextView mAccessCode;
	PatientReceiver mReceiver;
	PopupMenu popupmenu;

	
	private class PatientReceiver extends BroadcastReceiver
	{
		/**
		 * Hook method that's dispatched when the Login method returns.
		 */
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(TAG, "broadcastReceived...");
			if (intent.getIntExtra(StatusService.RESULTCODE, 0) == StatusService.RESULT_OK)
			{
				if (intent.hasExtra(StatusService.KEY_PATIENT))
				{
					Utils.showToast(getBaseContext(), "Data stored succesfully");
					Patient p = intent.getParcelableExtra(StatusService.KEY_PATIENT);
					getOps().setCurrentPatient(p);
					displayPatient(getOps().getCurrentPatient());
				}
				
			} else
			{
				Utils.showToast(getBaseContext(), "patient was not added...");
			}
			

		}
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, PatientActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_patient);
		initializeViewFields();
		mReceiver = new PatientReceiver();
		super.onCreate(savedInstanceState, PatientOps.class, this);
	}

	public void initializeViewFields()
	{
		mOkaybutton = (ImageView) findViewById(R.id.okay_patientact);
		mNewpatientbutton = (ImageView) findViewById(R.id.plus_patientact);
		mMenudotsbutton = (ImageView) findViewById(R.id.menudots_patientact);
		mFirstname = (EditText) findViewById(R.id.edittext_firstname);
		mLastname = (EditText) findViewById(R.id.edittext_lasstname);
		mAppointmentdate = (TextView) findViewById(R.id.edittext_appointmentdate);
		mBirthdate = (TextView) findViewById(R.id.edittext_birthdate);
		mMedicalRecordId = (TextView) findViewById(R.id.tv_mrid_value);
		mAccessCode = (TextView) findViewById(R.id.tv_accescode_value);
		View[] views = { mOkaybutton, mNewpatientbutton, mAppointmentdate, mBirthdate };
		makeClickListeners(views);
		createPopupMenu();
		
	}

	public void displayPatient(Patient patient)
	{
		if (patient != null)
		{
			Log.d(TAG, "displaying known data");
			mFirstname.setText(patient.getFirstName());
			mLastname.setText(patient.getLastName());
			mAppointmentdate.setText(Utils.convertTimeMillisToReadableFormat(patient.getAppointmentDate()));
			mBirthdate.setText(Utils.convertTimeMillisToReadableFormatNoHouraAndMinutes(patient.getBirthDate()));
			mMedicalRecordId.setText(patient.getMedicalRecordId());
			mAccessCode.setText(patient.getAccessCode());
		} else
		{
			Log.d(TAG, "Patient could not be displayed");
		}
	}

	
	/**
	 * Minimum text validation;
	 * TODO : Expand on more solid validation. 
	 */
	public static boolean validText(TextView editText)
	{
		String text = editText.getText().toString().trim();
		editText.setError(null);

		if (text.length() == 0)
		{
			return false;
		}
		if (text.length() > 25)
		{
			return false;
		}
		return true;
	}

	public void makeClickListeners(View[] views)
	{
		for (View v : views)
		{
			v.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					generalClickListener(v);
				}
			});
		}
	}
/**
 *General purpose method to bring clickevents to a centralized place. 
 */
	public void generalClickListener(View v)
	{
		switch (v.getId())
		{
		case (R.id.okay_patientact):
		{
			Log.d(TAG, "OKAY CLICKED");
			Intent i = getIntent();
			String mrid = getOps().getCurrentPatient().getMedicalRecordId();
			if (i.hasExtra(StatusService.REQUESTCODE) || mrid.length() > 0)
			{
				Log.d(TAG, "HAS REQUESTCODE EXTRA");

				if (i.getIntExtra(StatusService.REQUESTCODE, 2) == PatientListActivity.REQUEST_EDIT_PATIENT
						|| i.getIntExtra(StatusService.REQUESTCODE, 2) == SessionViewActivity.REQUESTCODE_MAKEAPPOINTMENT || mrid.length() > 0)
				{
					Log.d(TAG, "REQUESTCODE FOR EDITING EXISTING PATIENT OR MAKING AN APPOINTMENT");
					if (!validText(mFirstname) || !validText(mLastname))
					{
						Utils.showToast(getActivityContext(), "Names must be valid entries");
						return;
					}
					Patient p2 = getOps().getCurrentPatient();
					p2.setFirstName(mFirstname.getText().toString());
					p2.setLastName(mLastname.getText().toString());
					getOps().setCurrentPatient(p2);
					Intent intent2 = StatusService.makeIntent(getActivityContext(), StatusService.REQ_EDITPATIENT);
					intent2.putExtra(StatusService.KEY_PATIENT, p2);
					startService(intent2);
					break;
				}
				
			}
			
			
			Log.d(TAG, "NO INTENT, JUST ADDING A NEW PATIENT");
			if (!validText(mFirstname) || !validText(mLastname))
			{
				Utils.showToast(getActivityContext(), "Names must be valid entries");
				return;
			}
			Patient p = getOps().getCurrentPatient();
			p.setFirstName(mFirstname.getText().toString());
			p.setLastName(mLastname.getText().toString());
			getOps().setCurrentPatient(p);

			Intent intent = StatusService.makeIntent(getActivityContext(), StatusService.REQ_NEWPATIENT);
			intent.putExtra(StatusService.KEY_PATIENT, p);
			startService(intent);
			break;
		}
		case (R.id.plus_patientact):
		{
			getIntent().removeExtra(StatusService.REQUESTCODE);
			clearAllFields();
			break;
		}
		case (R.id.edittext_appointmentdate):
		{
			DialogFragment dialogFragment = new StartDatePicker();
			dialogFragment.show(getFragmentManager(), "start_date_and_then_time_picker");
			break;
		}
		case (R.id.edittext_birthdate):
		{
			DialogFragment dialogFragment = new StartDatePicker();
			dialogFragment.show(getFragmentManager(), "start_date_picker");
			break;
		}

		}
	}

	
	public void createPopupMenu()
	{
		popupmenu = new PopupMenu(this, findViewById(R.id.menudots_patientact));
		popupmenu.getMenuInflater().inflate(R.menu.main3, popupmenu.getMenu());
		popupmenu.setOnMenuItemClickListener(new MenuClicker());
		mMenudotsbutton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				popupmenu.show();
			}
		});
	}

	
	/**
	 * Menu items for the dots menu on the upper right.
	 */
	class MenuClicker implements PopupMenu.OnMenuItemClickListener
	{
		@Override
		public boolean onMenuItemClick(MenuItem item)
		{
			switch (item.getItemId())
			{
			case R.id.action_gotosessionviewactivity:
			{
				Utils.showToast(getBaseContext(), "back to queue");
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(SessionViewActivity.makeIntent(getBaseContext()));
				finish();
				break;
			}
			case R.id.action_gotopatientlistactivity:
			{
				Utils.showToast(getBaseContext(), "patientlist");
				Intent intent = PatientListActivity.makeIntent(getApplicationContext());
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intent);
				finish();
				break;
			}
			case R.id.action_gotosettingsactivity:
			{
				Intent intentsettings = SettingsActivity.makeIntent(getActivityContext());
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intentsettings);
				Utils.showToast(getBaseContext(), "settings");
				break;
			}
			case R.id.action_gotohelpscreens:
			{
				Intent intent = HelpActivity.makeIntent(getApplicationContext());
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intent);
				Utils.showToast(getBaseContext(), "help");
				break;
			}
			case R.id.action_logout:
			{
				Intent intent = LoginActivity.makeIntent(getActivityContext());
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intent);
				Utils.showToast(getBaseContext(), "loggedout");
				finish();
				break;
			}
			}
			return false;
		}
	}
	
	class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the current date as the default date in the picker
			DatePickerDialog dialog = new DatePickerDialog(PatientActivity.this, 0, this, Calendar.getInstance().get(
					Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH));
			return dialog;

		}

		
		/**
		 * When Time or Date is set we need to make a difference between a birthdate and an appointmentdate;
		 * When we are dealing with an appointmentdate we want also to know the time, and the case of a
		 * birthday we are done.
		 */
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			String source = getTag();
			switch (source)
			{
			case ("start_date_picker"):
			{
				saveTheView();
				long date = Utils.makeDateAsMillis(year, monthOfYear, dayOfMonth);
				getOps().getCurrentPatient().setBirthDate(date);
				displayPatient(getOps().getCurrentPatient());
				break;
			}
			case("start_date_and_then_time_picker"):
			{
				long date = Utils.makeDateAsMillis(year, monthOfYear, dayOfMonth);
				getOps().getCurrentPatient().setAppointmentDate(date);
				dismiss();
				DialogFragment timepicker = new StartTimePicker();
				timepicker.show(getFragmentManager(), "start_time_picker");
				break;
			}
			
			}
		}
	}

	class StartTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener
	{
		 @Override
		    public Dialog onCreateDialog(Bundle savedInstanceState) {
		        // Use the current time as the default values for the picker
		        final Calendar c = Calendar.getInstance();
		        int hour = c.get(Calendar.HOUR_OF_DAY);
		        int minute = c.get(Calendar.MINUTE);

		        // Create a new instance of TimePickerDialog and return it
		        return new TimePickerDialog(getActivity(), this, hour, minute,
		                DateFormat.is24HourFormat(getActivity()));
		    }

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			saveTheView();
			long time = Utils.makeTimeAsMillis(hourOfDay, minute);
			long appointmenttime = getOps().getCurrentPatient().getAppointmentDate() + time;
			getOps().getCurrentPatient().setAppointmentDate(appointmenttime);
			displayPatient(getOps().getCurrentPatient());
		}

	}

	
	/**
	 * Before each display the current views have to be validated and saved in the PatientOps() object.
	 */
	public void saveTheView()
	{
		if(validText(mFirstname))
		{
			getOps().getCurrentPatient().setFirstName(mFirstname.getText().toString());
		}
		if(validText(mLastname))
		{
			getOps().getCurrentPatient().setLastName(mLastname.getText().toString());
		}
	}
	

	private void showBirthDate(int year, int month, int day)
	{
		mBirthdate.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
	}

	private void showAppointmentDate(int year, int month, int day)
	{
		mAppointmentdate.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
	}

	public void clearAllFields()
	{
		long today = new GregorianCalendar().getTimeInMillis();
		Patient p = new Patient("", "", "", "", today, "", today);
		getOps().setCurrentPatient(p);
		displayPatient(getOps().getCurrentPatient());
	}

	@Override
	public Intent getTheIntent()
	{
		return getIntent();
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
		IntentFilter intentFilter = new IntentFilter(StatusService.EDITPATIENT);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
	}

}
