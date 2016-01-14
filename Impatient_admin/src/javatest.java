

public class javatest
{
	public static void main(String[] args)
	{
	Patient p;	
		for(int i = 0 ; i <100; i++)
		{
			p = Utils.makeRandomPatient();
			System.out.println(p.toString());
			String normaldate = Utils.convertTimeMillisToReadableFormat(p.getAppointmentDate());
			String appointmentdate = Utils.makeDateForSearchString(p.getAppointmentDate());
			System.out.println(appointmentdate);
		}
	}
}
