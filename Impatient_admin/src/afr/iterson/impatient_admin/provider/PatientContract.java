package afr.iterson.impatient_admin.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Video database.
 */
public final class PatientContract {
    /**
     * The "Content authority" is a name for the entire content
     * provider, similar to the relationship between a domain name and
     * its website.  A convenient string to use for the content
     * authority is the package name for the application, which must be unique
     * on the device.
     */
    public static final String CONTENT_AUTHORITY = "afr.iterson.patientprovider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's that applications
     * will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible
     * URI's), e.g., content://vandy.mooc/video/ is a valid path for
     * Video data. However, content://vandy.mooc/givemeroot/ will
     * fail since the ContentProvider hasn't been given any
     * information on what to do with "givemeroot".
     */
    public static final String PATH_PATIENT = PatientEntry.TABLE_NAME;

    /**
     * Inner class that defines the contents of the Video table.
     */
    public static final class PatientEntry implements BaseColumns {
        /**
         * Use BASE_CONTENT_URI to create the unique URI for Video
         * Table that applications will use to contact the content provider.
         */
        public static final Uri CONTENT_URI = 
			BASE_CONTENT_URI.buildUpon().appendPath(PATH_PATIENT).build(); 

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_TYPE =
            "vnd.android.cursor.dir/"
            + CONTENT_AUTHORITY 
            + "/" 
            + PATH_PATIENT;

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/"
            + CONTENT_AUTHORITY 
            + "/" 
            + PATH_PATIENT;

        /**
         * Name of the database table.
         */
        public static final String TABLE_NAME = "patient_table";

        /**
         * Columns to store the data of each Video uploaded from the client.
         */
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_FIRSTNAME = "firstname";
        public static final String COLUMN_LASTNAME = "lastname";
        public static final String COLUMN_MRID = "mrid";
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_APPOINTMENTDATE = "appointmentdate";
        public static final String COLUMN_ACCESSCODE = "accesscode";
        public static final String COLUMN_SEARCHSTRING = "searchstring";
        public static final String COLUMN_STRING1 = "string1";
        public static final String COLUMN_STRING2 = "string2";
        public static final String COLUMN_INTEGER1 = "string3";
        
        
        /**
         * Defines selection for all videos
         */
        public static String[] ALL_PATIENT_DATA = { _ID, PatientEntry.COLUMN_GENDER,
        		PatientEntry.COLUMN_FIRSTNAME,  PatientEntry.COLUMN_LASTNAME, PatientEntry.COLUMN_MRID,
        		PatientEntry.COLUMN_BIRTHDATE,PatientEntry.COLUMN_APPOINTMENTDATE,PatientEntry.COLUMN_ACCESSCODE
        };

        /**
         * Return a Uri that points to the row containing a given id.
         * 
         * @param id
         * @return Uri
         */
        public static Uri buildPatientUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
