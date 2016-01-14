package afr.iterson.impatient_admin.operations;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.aidl.PatientStatus;
import afr.iterson.impatient_admin.provider.PatientContract;
import afr.iterson.impatient_admin.utils.Utils;
import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts.People;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class PatientListCursorAdapter extends SimpleCursorAdapter implements Filterable
{
	public static final String TAG = PatientListCursorAdapter.class.getSimpleName();
	private Context context;

	private int layout;

	@SuppressWarnings("deprecation")
	public PatientListCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to)
	{
		super(context, layout, c, from, to);
		this.context = context;
		this.layout = layout;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{

		Cursor c = getCursor();

		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);

		int nameCol1 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_GENDER);
		String gender = c.getString(nameCol1);
		int nameCol2 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_FIRSTNAME);
		String firstname = c.getString(nameCol2);
		int nameCol3 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_LASTNAME);
		String lastname = c.getString(nameCol3);
		TextView fullname = (TextView) v.findViewById(R.id.patients_name);
		if (gender != null && firstname != null && lastname != null)
		{
			fullname.setText(gender + " " + firstname + " " + lastname);
		}

		int nameCol4 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_MRID);
		String mrid = c.getString(nameCol4);
		TextView medicalrecordid = (TextView) v.findViewById(R.id.patient_mrid);
		if (mrid != null)
		{
			medicalrecordid.setText(mrid);
		}

		int nameCol5 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_APPOINTMENTDATE);
		long appdate = c.getLong(nameCol5);
		String appodate = Utils.convertTimeMillisToReadableFormat(appdate);
		TextView appointmentdate = (TextView) v.findViewById(R.id.patient_app_time);
		if (appodate != null)
		{
			appointmentdate.setText(appodate);
		}

		return v;
	}

	@Override
	public void bindView(View v, Context context, Cursor c)
	{
		int nameCol1 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_GENDER);
		String gender = c.getString(nameCol1);
		int nameCol2 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_FIRSTNAME);
		String firstname = c.getString(nameCol2);
		int nameCol3 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_LASTNAME);
		String lastname = c.getString(nameCol3);
		TextView fullname = (TextView) v.findViewById(R.id.patients_name);
		if (gender != null && firstname != null && lastname != null)
		{
			fullname.setText(gender + " " + firstname + " " + lastname);
		}

		int nameCol4 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_MRID);
		String mrid = c.getString(nameCol4);
		TextView medicalrecordid = (TextView) v.findViewById(R.id.patient_mrid);
		if (mrid != null)
		{
			medicalrecordid.setText(mrid);
		}

		int nameCol5 = c.getColumnIndex(PatientContract.PatientEntry.COLUMN_APPOINTMENTDATE);
		long appdate = c.getLong(nameCol5);
		String appodate = Utils.convertTimeMillisToReadableFormat(appdate);
		TextView appointmentdate = (TextView) v.findViewById(R.id.patient_app_time);
		if (appodate != null)
		{
			appointmentdate.setText(appodate);
		}

	}

	
	public Patient getPatient(int position)
	{
		Cursor cursor = getCursor();
		Patient patient = null;
		if(cursor.moveToPosition(position))
		{
			patient = new Patient();
			patient.setGender(cursor.getString(cursor.getColumnIndex(PatientContract.PatientEntry.COLUMN_GENDER)));
			patient.setFirstName(cursor.getString(cursor.getColumnIndex(PatientContract.PatientEntry.COLUMN_FIRSTNAME)));
			patient.setLastName(cursor.getString(cursor.getColumnIndex(PatientContract.PatientEntry.COLUMN_LASTNAME)));
			patient.setMedicalRecordId(cursor.getString(cursor.getColumnIndex(PatientContract.PatientEntry.COLUMN_MRID)));
			patient.setAccessCode(cursor.getString(cursor.getColumnIndex(PatientContract.PatientEntry.COLUMN_ACCESSCODE)));
			patient.setBirthDate(cursor.getLong(cursor.getColumnIndex(PatientContract.PatientEntry.COLUMN_BIRTHDATE)));
			patient.setAppointmentDate(cursor.getLong(cursor.getColumnIndex(PatientContract.PatientEntry.COLUMN_APPOINTMENTDATE)));
			patient.setStatus(PatientStatus.unavailable);
		}
		return patient;
	}
	
	
	
	/**
	 * This method is invoked by the EditText search field that has a Listener on it.
	 * Each character is recognized as a search query and gives its results right away 
	 * in the UI. The query is made faster by adding a searchstring column in the patient table
	 * that consists of all searchable attributes like appointmenttimes and names. 
	 */
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint)
	{
		if (getFilterQueryProvider() != null)
		{
			return getFilterQueryProvider().runQuery(constraint);
		}

		StringBuilder buffer = null;
		String[] args = null;
		if (constraint != null)
		{
			buffer = new StringBuilder();
			buffer.append(PatientContract.PatientEntry.COLUMN_SEARCHSTRING);
			buffer.append(" LIKE ?");
			args = new String[] {  "%" + constraint.toString() + "%" };
		}
		return context.getContentResolver().query(PatientContract.PatientEntry.CONTENT_URI, null,
				buffer == null ? null : buffer.toString(), args,
				PatientContract.PatientEntry.COLUMN_SEARCHSTRING + " ASC");
	}

	/**
	 * select searchstring from patient_table where name like '%?%'
	 * 
	 * adding a special searchstring in the db
	 * select * from patient_table where searchstring MATCH "d e c"
	 */
	
	
	@Override
	public Filter getFilter()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
