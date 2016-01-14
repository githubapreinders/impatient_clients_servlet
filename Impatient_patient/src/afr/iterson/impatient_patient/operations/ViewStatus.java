package afr.iterson.impatient_patient.operations;


public enum ViewStatus 
{
	no_appointment, appointment_today, checked_in, temporary_unavailable, go_to_dressingroom,defaultvalue;

	 public  ViewStatus next()
	 {
		 if(this.ordinal() < ViewStatus.values().length -1)
		 {
			 return ViewStatus.values()[this.ordinal() + 1];
		 }
		 else
		 {
			 return ViewStatus.values()[0];
		 }
	 }
	
}
