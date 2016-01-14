

public enum PatientStatus
{
	unavailable, available, currently_unavailable, under_treatment;
	
	 public  PatientStatus next()
	 {
		 if(this.ordinal() < PatientStatus.values().length -1)
		 {
			 return PatientStatus.values()[this.ordinal() + 1];
		 }
		 else
		 {
			 return PatientStatus.values()[0];
		 }
	 }
	 
	 public static void main(String[] args)
	 {
		for(PatientStatus status : PatientStatus.values())
		{
			System.out.println(status + " next: " + status.next());
		} 
	 }
	 
	
}

 