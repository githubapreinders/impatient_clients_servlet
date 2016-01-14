package afr.iterson.impatient_patient.aidl;

import afr.iterson.impatient_patient.aidl.Patient;
import afr.iterson.impatient_patient.aidl.PatientResults;

/**
 * Interface defining the method implemented within
 * PollingService that provides asynchronous access to the
 * Spring web service.
 */
interface PatientRequest {
   /**
    * A one-way (non-blocking) call to the PollingService that
    * retrieves information about the currentpatient list from the Spring web service.  
    * The PollingService subsequently uses the
    * PatientListResults parameter to return a List of Patient
    * containing the results from theSpring web service back
    * to the SessionViewActivity via the one-way sendResults() method.
    */
    oneway void getPatient(in String medicalrecordid, in PatientResults results); 
}
