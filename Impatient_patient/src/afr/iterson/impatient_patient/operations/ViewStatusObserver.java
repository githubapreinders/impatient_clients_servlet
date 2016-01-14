package afr.iterson.impatient_patient.operations;

//Notifies that changes in the ViewStatus have been made; Necessary to display 
//an animation that depends on the Status of the Patient.
public interface ViewStatusObserver
{
	public void update(ViewStatus status);
}
