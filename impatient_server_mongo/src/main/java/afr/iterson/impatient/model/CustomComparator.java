package afr.iterson.impatient.model;

import java.util.Comparator;

public class CustomComparator implements Comparator<Patient>
{
	@Override
	public int compare(Patient p1, Patient p2)
	{
		Long l1 = (Long)p1.getAppointmentDate();
		Long l2 = (Long)p2.getAppointmentDate();
		return l1.compareTo(l2);
	}
}
