package afr.iterson.impatient_admin.activities;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.operations.PatientListCursorAdapter;
import afr.iterson.impatient_admin.operations.PatientListOps;
import afr.iterson.impatient_admin.services.StatusService;
import afr.iterson.impatient_admin.utils.GenericActivity;
import afr.iterson.impatient_admin.utils.Utils;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

public class PatientListActivity extends GenericActivity<PatientListOps.View, PatientListOps> implements
		PatientListOps.View
{

	public static final int REQUEST_NEW_IN_SESSION = 300;
	public static final int REQUEST_EDIT_PATIENT = 400;
	public static final int REQUEST_ADD_NEW_PATIENT = 500;

	private ListView patientList;
	private Toolbar toolbar;
	private ImageView optionsButton;
	private EditText searchBox;
	ImageView menuDotsButton;
	private LoaderManager lm;
	private PopupMenu popupmenu;

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, PatientListActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_patientlist);
		lm = getLoaderManager();
		initializeViews();
		super.onCreate(savedInstanceState, PatientListOps.class, this);
	}

	/**
	 * The patientlist is the important data type here. It gets its data from a
	 * content provider that gives a cursor to an attached CursorAdapter.
	 */
	public void initializeViews()
	{
		patientList = (ListView) findViewById(R.id.act_pat_patientList);
		toolbar = (Toolbar) findViewById(R.id.act_pat_toolbar);
		patientList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				Log.d(TAG, "Deciding to go to patient activitiy or to return to  session");
				Patient p = getOps().getmAdapter().getPatient(arg2);
				Intent thisintent = getIntent();
				if (thisintent.hasExtra(StatusService.REQUESTCODE))
				{
					if (thisintent.getIntExtra(StatusService.REQUESTCODE, 2) == SessionViewActivity.REQUESTCODE_INSERTPATIENT)
					{
						thisintent.putExtra(StatusService.KEY_PATIENT, p);
						setResult(Activity.RESULT_OK, thisintent);
						finish();
						return;
					}
				}
				Intent intent = PatientActivity.makeIntent(getActivityContext());
				intent.putExtra(StatusService.REQUESTCODE, REQUEST_EDIT_PATIENT);
				intent.putExtra(StatusService.KEY_PATIENT, p);
				startActivity(intent);
			}
		});
		menuDotsButton = (ImageView) findViewById(R.id.act_pat_greydots);
		// registerForContextMenu(menuDotsButton);
		searchBox = (EditText) findViewById(R.id.act_pat_edittext);
		searchBox.addTextChangedListener(new TextWatcher()
		{

			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			public void afterTextChanged(Editable s)
			{
				Cursor c = getOps().getmAdapter().runQueryOnBackgroundThread(s.toString());
				getOps().getmAdapter().swapCursor(c);
			}
		});

		createPopupMenu();
	}

	public void createPopupMenu()
	{
		popupmenu = new PopupMenu(this, findViewById(R.id.act_pat_greydots));
		popupmenu.getMenuInflater().inflate(R.menu.main2, popupmenu.getMenu());
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
				startActivity(SessionViewActivity.makeIntent(getBaseContext()));
				finish();
				break;
			}
			case R.id.action_gotoindivpatientactivity:
			{
				Utils.showToast(getBaseContext(), "New Patient");
				Intent intent = PatientActivity.makeIntent(getBaseContext());
				intent.putExtra(StatusService.REQUESTCODE, REQUEST_ADD_NEW_PATIENT);
				startActivity(intent);
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

//	/**
//	 * The Context menu that resides under the upper right corner button.
//	 */
//
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
//	{
//		super.onCreateContextMenu(menu, v, menuInfo);
//
//		menu.add(0, v.getId(), 0, getResources().getString(R.string.action_gotosessionviewactivity));
//		menu.add(0, v.getId(), 0, getResources().getString(R.string.action_makenewpatient));
//		menu.add(0, v.getId(), 0, getResources().getString(R.string.action_gotosettingsactivity));
//		menu.add(0, v.getId(), 0, getResources().getString(R.string.action_gotohelpscreens));
//		menu.add(0, v.getId(), 0, getResources().getString(R.string.action_logout));
//	}

	/**
	 * Selection of context items. TODO : converting hard coded values to
	 * variables.
	 */
//	@Override
//	public boolean onContextItemSelected(MenuItem item)
//	{
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//		String title = item.getTitle().toString();
//
//		switch (title)
//		{
//
//		case ("Queue"):
//		{
//			Utils.showToast(getBaseContext(), "back to queue");
//			startActivity(SessionViewActivity.makeIntent(getBaseContext()));
//			finish();
//			break;
//		}
//		case ("New Patient"):
//		{
//			Utils.showToast(getBaseContext(), "New Patient");
//			Intent intent = PatientActivity.makeIntent(getBaseContext());
//			intent.putExtra(StatusService.REQUESTCODE, REQUEST_ADD_NEW_PATIENT);
//			startActivity(intent);
//			break;
//		}
//		case ("Settings"):
//		{
//			Intent intentsettings = SettingsActivity.makeIntent(getActivityContext());
//			overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
//			startActivity(intentsettings);
//			Utils.showToast(getBaseContext(), "settings");
//			break;
//		}
//		case ("Help screen"):
//		{
//			Intent intent = HelpActivity.makeIntent(getApplicationContext());
//			overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
//			startActivity(intent);
//			Utils.showToast(getBaseContext(), "help");
//			break;
//		}
//		case ("Log out"):
//		{
//			Intent intent = LoginActivity.makeIntent(getActivityContext());
//			overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
//			startActivity(intent);
//			Utils.showToast(getBaseContext(), "loggedout");
//			break;
//		}
//
//		case ("Cancel"):
//		{
//			Utils.showToast(getBaseContext(), "cancelled");
//			break;
//		}
//
//		default:
//		{
//			Utils.showToast(getBaseContext(), "default");
//			break;
//		}
//
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void setAdapter(PatientListCursorAdapter sessionAdapter)
	{
		patientList.setAdapter(sessionAdapter);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "Opening the cursor");
		getLoaderManager().restartLoader(PatientListOps.LOADER_ID, null, getOps());

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (getOps().getmAdapter().getCursor() != null)
		{
			Log.d(TAG, "Closing the cursor");
			getOps().getmAdapter().getCursor().close();
		}
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
	}

	@Override
	public LoaderManager getViewLoaderManager()
	{
		return lm;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PatientListActivity.REQUEST_NEW_IN_SESSION)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				Patient p = data.getParcelableExtra(StatusService.KEY_PATIENT);
				Intent returnintent = SessionViewActivity.makeIntent(getBaseContext());
				data.putExtra(StatusService.KEY_PATIENT, p);
				setResult(Activity.RESULT_OK, returnintent);
				finish();
			}
		}

	}

}
