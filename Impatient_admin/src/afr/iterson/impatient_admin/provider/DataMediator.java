package afr.iterson.impatient_admin.provider;

import java.util.ArrayList;
import java.util.Collection;

import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.utils.Utils;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DataMediator
{

	private static final String TAG = DataMediator.class.getSimpleName();

	private ContentResolver resolver;

	public DataMediator(Context context)
	{
		this.resolver = context.getContentResolver();
	}

	public Cursor getWholePatientList()
	{
		Log.d(TAG, " Getting the list of all patients...");
		return resolver.query(PatientContract.PatientEntry.CONTENT_URI, 
				getNeededColumns(), 
				null, 
				null,
				null);
	}

	public static String[] getNeededColumns()
	{
		return new String[] { PatientContract.PatientEntry._ID, PatientContract.PatientEntry.COLUMN_GENDER,
				PatientContract.PatientEntry.COLUMN_FIRSTNAME, PatientContract.PatientEntry.COLUMN_LASTNAME,
				PatientContract.PatientEntry.COLUMN_MRID, PatientContract.PatientEntry.COLUMN_BIRTHDATE,
				PatientContract.PatientEntry.COLUMN_APPOINTMENTDATE, PatientContract.PatientEntry.COLUMN_ACCESSCODE,
				PatientContract.PatientEntry.COLUMN_SEARCHSTRING, };
	}

	// Insert that adds a newly made patient to the list.
	public void addPatientToContentProvider(Patient patient)
	{
		Log.d(TAG, " adding " + patient.getMedicalRecordId() + " to the contenprovider: ");
		resolver.insert(PatientContract.PatientEntry.CONTENT_URI, makeContentValues(patient));
	}

	// Standard insert with the list from the server
	public void addPatientListToContentProvider(Collection<Patient> patients)
	{
		Log.d(TAG, " adding " + patients.toString() + " to the contenprovider: ");
		resolver.bulkInsert(PatientContract.PatientEntry.CONTENT_URI, makeContentValuesArray(patients));
	}

	// For reasons of security needed to remove the whole list from the device
	// when the session closes or the app is paused
	public void deletePatientsList()
	{
		Log.d(TAG, "Deleting patient list");
		resolver.delete(PatientContract.PatientEntry.CONTENT_URI, null, null);
	}

	// Updating values after an update in the PatientActivity
	public void updatePatient(Patient patient)
	{
		Log.d(TAG, "Updating " + patient.makeFullName() + " MRID:" + patient.getMedicalRecordId());
		resolver.update(PatientContract.PatientEntry.CONTENT_URI, makeContentValues(patient),
				PatientContract.PatientEntry.COLUMN_MRID + "=?", new String[] { patient.getMedicalRecordId() });
	}

	public ContentValues[] makeContentValuesArray(Collection<Patient> patients)
	{
		ContentValues[] returnvalues = new ContentValues[patients.size()];
		ArrayList<Patient> patientlist = new ArrayList<Patient>(patients);
		int index = 0;
		for (Patient p : patientlist)
		{
			returnvalues[index] = makeContentValues(p);
			index++;
		}
		return returnvalues;
	}

	public ContentValues makeContentValues(Patient Patient)
	{
		String searchstring = Patient.getGender() + " " + Patient.getFirstName() + " " + Patient.getLastName() + " "
				+ Patient.getMedicalRecordId() + " " + Utils.makeDateForSearchString((Patient.getBirthDate()))
				+ " " + Utils.makeDateForSearchString(Patient.getAppointmentDate());

		final ContentValues cvs = new ContentValues();
		cvs.put(PatientContract.PatientEntry.COLUMN_GENDER, Patient.getGender());
		cvs.put(PatientContract.PatientEntry.COLUMN_FIRSTNAME, Patient.getFirstName());
		cvs.put(PatientContract.PatientEntry.COLUMN_LASTNAME, Patient.getLastName());
		cvs.put(PatientContract.PatientEntry.COLUMN_MRID, Patient.getMedicalRecordId());
		cvs.put(PatientContract.PatientEntry.COLUMN_BIRTHDATE, Patient.getBirthDate());
		cvs.put(PatientContract.PatientEntry.COLUMN_APPOINTMENTDATE, Patient.getAppointmentDate());
		cvs.put(PatientContract.PatientEntry.COLUMN_ACCESSCODE, Patient.getAccessCode());
		cvs.put(PatientContract.PatientEntry.COLUMN_SEARCHSTRING, searchstring);
		return cvs;
	}

}
