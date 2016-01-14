package afr.iterson.impatient_admin.activities;

import java.util.Calendar;
import java.util.Date;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.operations.SessionListAdapter;
import afr.iterson.impatient_admin.operations.SessionViewOps;
import afr.iterson.impatient_admin.services.StatusService;
import afr.iterson.impatient_admin.utils.GenericActivity;
import afr.iterson.impatient_admin.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

/**
 * This class supports the management of a list of patients that are waiting for
 * a treatment. The list is updated by regular calls to the server via an aidl
 * service that operates in a separate process. All manipulations to the queue
 * are reported to the server via an Intent Service. Communication from services
 * to this activity back go via a broadcastreceiver. The adapter that backs this
 * list as well as the aidl service are shielded from this class in a parallel
 * SessionViewOps class, to protect against runtime configuration changes, and
 * to enlarge transparency.
 * 
 * @author ap
 *
 */
public class SessionViewActivity extends GenericActivity<SessionViewOps.View, SessionViewOps> implements
		SessionViewOps.View
{

	private final static String TAG = SessionViewActivity.class.getSimpleName();
	public static final int REQUESTCODE_PATIENTLISTACTIVITY = 1;
	public static final int REQUESTCODE_INSERTPATIENT = 2;
	public static final int REQUESTCODE_MAKEAPPOINTMENT = 3;
	public static final String INTENT_EXTRA = "intent_extra";

	/**
	 * Views in this activity;
	 */
	private ImageView plusbutton;
	private ImageView arrowUpButton;
	private ImageView arrowDownButton;
	private ImageView menuDotsButton;
	private ListView sessionList;
	private PopupMenu popupmenu;

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, SessionViewActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_sessionview);
		initializeViewFields();
		super.onCreate(savedInstanceState, SessionViewOps.class, this);

	}

	public void initializeViewFields()
	{
		sessionList = (ListView) findViewById(R.id.sessionsList);
		sessionList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				highlightListItem(arg2);
				Utils.showToast(getBaseContext(), "item " + arg2 + " selected");
			}
		});
		View footerView = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.footer_sessionlist, null, false);
		sessionList.addFooterView(footerView);
		footerView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startService(StatusService.makeIntent(getApplicationContext(), StatusService.REQ_STOPSESSION));
				finish();
			}
		});
		registerForContextMenu(sessionList);

		plusbutton = (ImageView) findViewById(R.id.plus);
		plusbutton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				plus(v);
			}
		});

		arrowUpButton = (ImageView) findViewById(R.id.arrowup);
		arrowUpButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				arrowUp(v);
			}
		});

		arrowDownButton = (ImageView) findViewById(R.id.arrowdown);
		arrowDownButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				arrowDown(v);
			}
		});

		menuDotsButton = (ImageView) findViewById(R.id.menudots);
		createPopupMenu();

	}

	public void createPopupMenu()
	{
		popupmenu = new PopupMenu(this, findViewById(R.id.menudots));
		popupmenu.getMenuInflater().inflate(R.menu.main, popupmenu.getMenu());
		popupmenu.setOnMenuItemClickListener(new MenuClicker());
		menuDotsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				popupmenu.show();
			}
		});
	}

/**
 *Menu items for the dots menu on the upper right. 
 */
	class MenuClicker implements PopupMenu.OnMenuItemClickListener
	{
		@Override
		public boolean onMenuItemClick(MenuItem item)
		{
			switch (item.getItemId())
			{
			case R.id.action_gotopatientlistactivity:
			{
				Intent intent = PatientListActivity.makeIntent(getBaseContext());
				intent.putExtra(StatusService.REQUESTCODE, REQUESTCODE_PATIENTLISTACTIVITY);
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intent);
				break;
			}
			case R.id.action_gotoindivpatientactivity:
			{
				Intent intentapp = PatientActivity.makeIntent(getBaseContext());
				int s = getOps().getmAdapter().getSelectedItem();
				Patient p = getOps().getmAdapter().getItem(s);
				intentapp.putExtra(StatusService.KEY_PATIENT, p);
				intentapp.putExtra(StatusService.REQUESTCODE, REQUESTCODE_MAKEAPPOINTMENT);
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intentapp);
				break;
			}
			case R.id.action_gotohelpscreens:
			{
				Intent intent = HelpActivity.makeIntent(getApplicationContext());
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intent);
				break;
			}
			case R.id.action_gotosettingsactivity:
			{
				Intent intentsettings = SettingsActivity.makeIntent(getActivityContext());
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intentsettings);
				break;
			}
			case R.id.action_logout:
			{
				Intent intent = LoginActivity.makeIntent(getActivityContext());
				overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
				startActivity(intent);
				finish();
				break;
			}
			}
			return false;

		}
	}


	/**
	 * A context menu for removing a patient; this makes sure that when removing 
	 * a patient from the list you can see which item will be deleted.
	 */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			Patient p = (Patient) sessionList.getItemAtPosition(info.position);
			menu.setHeaderIcon(R.drawable.ic_delete);
			menu.setHeaderTitle(p.getGender() + " " + p.getLastName());
			menu.add(0, v.getId(), 0, getResources().getString(R.string.action_delete));
			menu.add(0, v.getId(), 0, getResources().getString(R.string.action_cancel));

	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		String title = item.getTitle().toString();

		switch (title)
		{

		case ("Remove"):
		{
			Patient patient = (Patient) sessionList.getItemAtPosition(info.position);
			getOps().getmAdapter().remove(patient);
			Intent i = StatusService.makeIntent(getApplicationContext(), StatusService.REQ_DELETEFROMSESSION);
			i.putExtra(StatusService.KEY_PATIENT, patient);
			startService(i);
			break;
		}
		case ("Cancel"):
		{
			break;
		}

		default:
		{
			break;
		}

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Moves the selected patient upward through the queue.
	 * 
	 * @param v
	 */
	public void arrowUp(View v)
	{
		int currentitem = getOps().getmAdapter().getSelectedItem();
		int newposition = currentitem - 1;
		boolean succes = getOps().getmAdapter().swap(currentitem, newposition);
		if (succes)
		{
			highlightListItem(newposition);
			startService(StatusService.makeIntent(getBaseContext(), StatusService.REQ_MOVEUP).putExtra(
					StatusService.KEY_PATIENT, getOps().getmAdapter().getItem(newposition)));
		}
	}

	/**
	 * Moves the selected patient downward through the queue.
	 * 
	 * @param v
	 */
	public void arrowDown(View v)
	{

		int currentitem = getOps().getmAdapter().getSelectedItem();
		int newposition = currentitem + 1;
		boolean succes = getOps().getmAdapter().swap(currentitem, newposition);
		if (succes)
		{
			highlightListItem(newposition);
			startService(StatusService.makeIntent(getBaseContext(), StatusService.REQ_MOVEDOWN).putExtra(
					StatusService.KEY_PATIENT, getOps().getmAdapter().getItem(newposition)));
		}
	}

	/**
	 * Starts a new activity for a new patient to insert.
	 * 
	 * @param v
	 */
	public void plus(View v)
	{
		Utils.showToast(getBaseContext(), "insert patient, new activity started");
		Intent intent = PatientListActivity.makeIntent(getBaseContext());
		intent.putExtra(StatusService.REQUESTCODE, REQUESTCODE_INSERTPATIENT);
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
		startActivityForResult(intent, REQUESTCODE_INSERTPATIENT);

	}

	/**
	 * Returns a Patient object that is retrieved from the list of patients or
	 * from a newly created one. This object will be inserted into the list.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUESTCODE_INSERTPATIENT)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				Patient result = data.getParcelableExtra(StatusService.KEY_PATIENT);
				result.setAppointmentDate(Utils.makeAppointmentDateForToday().getTimeInMillis());
				Log.d(TAG, "inserting into session:" + result.getGender() + " " + result.getLastName());
				getOps().getmAdapter().insert(result, getOps().getmAdapter().getSelectedItem());
				startService(StatusService.makeIntent(getBaseContext(), StatusService.REQ_INSERTINSESSION)
						.putExtra(StatusService.KEY_POSITION, getOps().getmAdapter().getSelectedItem())
						.putExtra(StatusService.KEY_PATIENT, result));
			}
			if (resultCode == Activity.RESULT_CANCELED)
			{
				Utils.showToast(getBaseContext(), "No patient is inserted");
			}
		}
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
	protected void onPause()
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

	
	
	
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void setAdapter(SessionListAdapter sessionAdapter)
	{
		sessionList.setAdapter(sessionAdapter);
		// sessionList.setItemChecked(2, true);
	}

	private void highlightListItem(int position)
	{
		getOps().getmAdapter().setSelectedItem(position);
	}


}
