package afr.iterson.impatient_patient.operations;

import java.util.ArrayList;
import java.util.List;

//This class holds a ViewStatus as well as a list of observers who want to 
//observe this class. 
public class MyViewStatus implements ViewStatusSubject
{
	private ViewStatus status;
	
	private List<ViewStatusObserver> list = new ArrayList<ViewStatusObserver>();
	
	
	
	@Override
	public void notifyObservers()
	{
		for(ViewStatusObserver observer: list)
		{
			observer.update(status);
		}
	}

	@Override
	public void registerObserver(ViewStatusObserver observer)
	{
		list.add(observer);
	}

	@Override
	public void unregisterObserver(ViewStatusObserver observer)
	{
		int i = list.indexOf(observer);
		list.remove(i);
		
	}

	public ViewStatus getStatus()
	{
		return status;
	}

	public void setStatus(ViewStatus status)
	{
		this.status = status;
		notifyObservers();
	}
	
	
}
