package afr.iterson.impatient_admin.model;


/**
 * Holds a Timer and a Task object to keep track of the treatment time; The Task
 * just counts down from beginning to zero. The machine monitors the remaining
 * time and gives it to the Waiting Queue for calculation. More then one machine
 * can be started. In its constructor the machine needs its treatment time in
 * long value milliseconds.
 * 
 * @author ap
 *
 */
public class Machine
{

	public static long treatmentTime;
	public long remainingTreatmentTime;
	public boolean running;

	public Machine(long time)
	{
		Machine.treatmentTime = time;
		this.remainingTreatmentTime = treatmentTime;
	}

	class countDownTimer implements Runnable
	{

		@Override
		public void run()
		{
			System.out.println("Machine started");
			while (isRunning())
			{
				do
				{
					remainingTreatmentTime -= 1000;
					// System.out.println(remainingTreatmentTime);
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				} while (remainingTreatmentTime > 0);
				setRunning(false);
			}
		}
	}

	public void start()
	{

		Thread countingThread = new Thread(new countDownTimer());
		setRunning(true);
		countingThread.start();
	}

	public void stop()
	{
		setRunning(false);
	}

	public long getRemainingTreatmentTime()
	{
		return remainingTreatmentTime;
	}

	public void setRemainingTreatmentTime(long remainingTreatmentTime)
	{
		this.remainingTreatmentTime = remainingTreatmentTime;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	//resets all values to the defaults with these settings
	public void reset()
	{
		this.setRunning(false);
		this.setRemainingTreatmentTime(treatmentTime);
	}

}
