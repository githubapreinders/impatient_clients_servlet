package afr.iterson.impatient_patient.aidl;

import afr.iterson.impatient_patient.aidl.Patient;
import java.util.List;

/**
 * Interface defining the method that receives callbacks from the
 * PollingService.  This method should be implemented by the
 * PatientActivity.
 */
interface PatientResults {
    /**
     * This one-way (non-blocking) method allows PollingService
     * to return the  Patient associated with a
     * one-way PatientRequest.getPatient() call.
     */
    oneway void sendResults(in List<Patient> results);
}
