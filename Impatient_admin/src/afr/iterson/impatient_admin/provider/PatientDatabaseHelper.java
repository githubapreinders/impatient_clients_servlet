package afr.iterson.impatient_admin.provider;

import java.io.File;

import afr.iterson.impatient_admin.provider.PatientContract.PatientEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for Video data.
 */
public class PatientDatabaseHelper extends SQLiteOpenHelper {
    /**
     * If the database schema is changed, the database version must be
     * incremented.
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Database name.
     */
    public static final String DATABASE_NAME = "patient.db";

    /**
     * Constructor for PatientDatabaseHelper.  Store the database in
     * the cache directory so Android can remove it if memory is low.
     * 
     * @param context
     */
    public PatientDatabaseHelper(Context context) {
    	super(context, 
              context.getCacheDir()
              + File.separator 
              + DATABASE_NAME,
              null, 
              DATABASE_VERSION);
    }
    

    /**
     * Hook method called when Database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Define an SQL string that creates a table to hold Videos.
        // Each Video contains the video's id, title, duration, contentType, 
		// data_url, and star_rating
        final String SQL_CREATE_PATIENT_TABLE =
            "CREATE TABLE "
            + PatientEntry.TABLE_NAME + " (" 
            + PatientEntry._ID + " INTEGER PRIMARY KEY, " 
            + PatientEntry.COLUMN_GENDER + " TEXT NOT NULL, " 
            + PatientEntry.COLUMN_FIRSTNAME + " TEXT NOT NULL, "
            + PatientEntry.COLUMN_LASTNAME + " TEXT NOT NULL, "
            + PatientEntry.COLUMN_MRID + " TEXT NOT NULL, "
            + PatientEntry.COLUMN_BIRTHDATE + " INTEGER NOT NULL, "
            + PatientEntry.COLUMN_APPOINTMENTDATE + " INTEGER NOT NULL, "
            + PatientEntry.COLUMN_ACCESSCODE + " INTEGER NOT NULL, " 
            + PatientEntry.COLUMN_SEARCHSTRING + " TEXT NOT NULL, " 
            + PatientEntry.COLUMN_STRING1 + " TEXT , " 
            + PatientEntry.COLUMN_STRING2 + " TEXT , "
            + PatientEntry.COLUMN_INTEGER1 + " INTEGER "
            + " );";
        
        // Create the table.
        db.execSQL(SQL_CREATE_PATIENT_TABLE);
    }

    /**
     * Hook method called when Database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        // This database is only a cache for online data, so its
        // upgrade policy is to simply to discard the data and start
        // over.  This method only fires if you change the version
        // number for your database.  It does NOT depend on the
        // version number for your application.  If the schema is
        // updated without wiping data, commenting out the next 2
        // lines should be the top priority before modifying this
        // method.
        db.execSQL("DROP TABLE IF EXISTS " 
                   + PatientEntry.TABLE_NAME);
        onCreate(db);
    }
}
