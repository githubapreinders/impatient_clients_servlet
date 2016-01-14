package afr.iterson.impatient_patient.operations;

public interface ViewStatusSubject
{

	void notifyObservers();
	
	void registerObserver(ViewStatusObserver observer);
	
	void unregisterObserver(ViewStatusObserver observer);
	
}
