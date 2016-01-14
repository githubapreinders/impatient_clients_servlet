package afr.iterson.impatient.model;

public class AdminSettings
{
	public long treatmentTime;
	public int amountOfMachines;
	private static long MINUTES = 15;
	private static long TREATMENT_TIME = MINUTES*1000*60;
	private static int AMOUNT_OF_MACHINES = 1;
	
	public AdminSettings()
	{
		this.treatmentTime = TREATMENT_TIME;
		this.amountOfMachines = AMOUNT_OF_MACHINES;
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
	
	
}
