package afr.iterson.impatient_admin.aidl;

import afr.iterson.impatient_admin.aidl.Patient;
import java.util.List;

/**
 * Interface defining the method that receives callbacks from the
 * PollingService.  This method should be implemented by the
 * SessionViewActivity.
 */
interface PatientListResults {
    /**
     * This one-way (non-blocking) method allows PollingService
     * to return the List of Patient results associated with a
     * one-way PatientListRequest.getPatientList() call.
     */
    oneway void sendResults(in List<Patient> results);
}
