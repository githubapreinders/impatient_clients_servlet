package afr.iterson.impatient_admin.model;

import java.util.TimerTask;

public class MachineTask extends TimerTask
{
	public long remainingTreatmentTime;

	public MachineTask()
	{
	}

	@Override
	public void run()
	{
		remainingTreatmentTime -= 1000;
		if (remainingTreatmentTime <= 0)
		{
			cancel();
		}
		System.out.println("Remaining treatment time: " + remainingTreatmentTime);

	}

	public long getRemainingTreatmentTime()
	{
		return remainingTreatmentTime;
	}

	public void setRemainingTreatmentTime(long remainingTreatmentTime)
	{
		this.remainingTreatmentTime = remainingTreatmentTime;
	}
	
	
}
