package afr.iterson.impatient_admin.model;

/**
 * Holds the current settings for treatment time and amount of 
 * machines the center has.  
 * TODO : Store these values server side and protect them via
 * a super_admin user to prevent unwanted changes. 
 */
public class AdminSettings
{
	public long treatmentTime;
	public int amountOfMachines;
	public int  MINUTES = 15;
	private static long TREATMENT_TIME = 15*1000*60;
	private static int AMOUNT_OF_MACHINES = 1;
	
	public AdminSettings()
	{
		this.treatmentTime = TREATMENT_TIME;
		this.amountOfMachines = AMOUNT_OF_MACHINES;
	}
	
	public AdminSettings(int machines, int minutes)
	{
		this.MINUTES = minutes;
		this.amountOfMachines = machines;
	}

	
	
	
	public long getTreatmentTime()
	{
		return treatmentTime;
	}
	public void setTreatmentTime(int treatmentTime)
	{
		this.treatmentTime = treatmentTime*1000*60;
	}
	public int getAmountOfMachines()
	{
		return amountOfMachines;
	}
	public void setAmountOfMachines(int amountOfMachines)
	{
		this.amountOfMachines = amountOfMachines;
	}


	public int getMINUTES()
	{
		return MINUTES;
	}


	public void setMINUTES(int mINUTES)
	{
		MINUTES = mINUTES;
	}
	
	
}
