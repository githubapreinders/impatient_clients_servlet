package afr.iterson.impatient_admin.aidl;

import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.aidl.PatientListResults;

/**
 * Interface defining the method implemented within
 * PollingService that provides asynchronous access to the
 * Spring web service.
 */
interface PatientListRequest {
   /**
    * A one-way (non-blocking) call to the PollingService that
    * retrieves information about the currentpatient list from the Spring web service.  
    * The PollingService subsequently uses the
    * PatientListResults parameter to return a List of Patient
    * containing the results from theSpring web service back
    * to the SessionViewActivity via the one-way sendResults() method.
    */
    oneway void getPatientList(in PatientListResults results); 
}
