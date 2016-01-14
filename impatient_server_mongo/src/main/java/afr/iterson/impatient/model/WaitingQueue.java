package afr.iterson.impatient.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class WaitingQueue
{

	// TODO connect the waitingqueue with the repo; when the queue is made
	// it has to check whether there is a copy in the database/repository
	// wiring up inside the constructor gives an error...
	// @Autowired
	// MongoWaitingQueueRepo repo;

	// No @Id annotation for the time being; we only want the latest instance to
	// be persisted in order
	// To restore it later on after for instance a server restart.
	private static long id = 1;
	private boolean sessionStarted;

	public static long SLEEPING_TIME = 5000;

	// The CopyOnWriteArrayList is thread safe and has to be preferred above the
	// normal arraylist.
	private CopyOnWriteArrayList<Patient> queue;

	// The settings of the queue contain the waiting time and the amount of
	// machines in the treatment center.
	private AdminSettings settings;

	private Machine[] machines;

	long[] shortestWaitingTimes;

	public WaitingQueue()
	{
		queue = new CopyOnWriteArrayList<Patient>();
		settings = new AdminSettings();
	}

	public WaitingQueue(CopyOnWriteArrayList<Patient> patients)
	{
		this.queue = patients;
		sort();
	}

	/**
	 * This Runnable takes charge of adapting the waiting times and changing the
	 * queue. The runnable does this every 10 seconds.
	 * 
	 * @author ap
	 *
	 */
	private class QueueAdapter implements Runnable
	{
		@Override
		public void run()
		{
			System.out.println("Started checking the queue. Interval time " + SLEEPING_TIME + " millis");
			shortestWaitingTimes = new long[settings.getAmountOfMachines()];
			addMachines();
			sort();
			do
			{
				adaptWaitingTimes();
				try
				{
					Thread.sleep(SLEEPING_TIME);
				} catch (InterruptedException e)
				{
					System.out.println("thread effectively interrupted");
					setSessionStarted(false);
					e.printStackTrace();
				}
			} while (sessionStarted == true);
			System.out.println("exiting queue monitoring, stopping thread...");
			System.out.println("Closing down machines and resetting to the defaults...\n");
			for (Machine m : machines)
			{
				m.reset();
			}
		}

	}

	public void startThread()
	{
		Thread monitorThread = new Thread(new QueueAdapter());
		monitorThread.start();
	}

	/**
	 * When the status of a patient changes to "under treatment" we need to know if machines are available.
	 * If not we are resetting the status of this patient. 
	 * If so a timer is started (inside the Machine object) to count down to zero. The value of these timers
	 * is consulted in the adaptWaitingTimes() method.
	 * We also need to know if we are dealing with the case that a patient is not removed from the queue but
	 * his status is changed from "under treatment" to something else. In that case we need to reset that Machine.
	 */
	public synchronized void changeStatusToAddMachine(Patient p)
	{
		if (p.getStatus() == PatientStatus.under_treatment)
		{
			boolean started = false;
			for (int i = 0; i < machines.length; i++)
			{
				if (!(machines[i].isRunning()))
				{
					System.out.println("machine started for patient...\n");
					machines[i].setCurrentPatient(p.getMedicalRecordId());
					machines[i].start();
					started = true;
					break;
				}
			}
			if (!started)
			{
				System.out.println("No machine available for this patient, reverting the status to available");
				find(p).setStatus(PatientStatus.available);
			} else
			{
				System.out.println("Moving patient under treatment to the front of the queue if necessary");
				queue.remove(queue.indexOf(p));
				queue.add(0, p);
			}
		} else
		{
			for (Machine m : machines)
			{
				if (m.getCurrentPatient().equals(p.getMedicalRecordId()))
				{
					System.out.println("machine released, patient status NOT under treatment...");
					m.reset();
					break;
				}
			}
		}

	}

	// Makes a basic sort according to apointment time; earliest time is top of
	// the list.
	public void sort()
	{
		Collections.sort(queue, new CustomComparator());
	}

	/**
	 * Every patient that is in the queue has a predicted waiting time;in this
	 * method we are applying the algorithm to calculate this. First the
	 * beginning of the row is adapted to make sure that there are only patients
	 * with status available or status under treatment. Then we check wether
	 * there are machines running/patients under treatment. We grab their
	 * remaining processing time and put them in an array. The total waiting
	 * time now is remaining time in one of the machines + totalTreatment time *
	 * people before me in the queue
	 * 
	 * 
	 */
	public void adaptWaitingTimes()
	{
		System.out.println("Waiting queue : adapt waiting times\n");
		moveFirstAvailablePatientsToFront();

		int length = 0;
		// Check how many machines are running; the running machines remaining
		// times are put in an array.
		for (int i = 0; i < shortestWaitingTimes.length; i++)
		{
			if (machines[i].isRunning())
			{
				shortestWaitingTimes[i] = machines[i].getRemainingTreatmentTime();
				length++;
			}
		}

		int divider = length;
		if (divider == 0)
		{
			divider = 1;
		}
		int index = 0;
		int awarded = 0;

		for (Patient patient : queue)
		{

			switch (patient.getStatus())
			{
			case available:
			{
				patient.setWaitingTime(shortestWaitingTimes[index] + (settings.treatmentTime * awarded) / divider);
				awarded++;
				index++;
				if (index >= length)
				{
					index = 0;
				}
				break;
			}
			case currently_unavailable:
			{
				patient.setWaitingTime(shortestWaitingTimes[index] + (settings.treatmentTime * awarded) / divider);
				awarded++;
				index++;
				if (index >= length)
				{
					index = 0;
				}
				break;

			}
			case unavailable:
			{
				patient.setWaitingTime(0);
				break;
			}
			case under_treatment:
			{
				for (Machine m : machines)
				{
					if (m.getCurrentPatient().equals(patient.getMedicalRecordId()))
					{
						patient.setWaitingTime(m.getRemainingTreatmentTime());
					}
				}
				break;
			}
			default:
				break;

			}

		}
		// System.out.println(representQueue());
		System.out.println("............");
		printList(queue);
	}

	/**
	 * We move the first two available patients to the front of the queue to
	 * make sure that there are always two persons to call. 
	 * TODO: Let the amount of available people that are put forward be dependent upon the amount of machines.
	 */
	public void moveFirstAvailablePatientsToFront()
	{

		// Make a list with indexes of patients that are available; we only need
		// two items or less in this list.
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		int index = 0;
		for (Patient p : queue)
		{
			if (p.getStatus() == PatientStatus.available)
			{
				indexes.add(queue.indexOf(p));
				index++;
			}
			if (index > 1)
			{
				break;
			}
		}
		// check whether there are patients with wrong statuses at the beginning
		// of the queue; in that case we will
		// swap them with the indexes in the list.
		switch (indexes.size())
		{
		case 0:
		{
			return;
		}
		case 1:
		{
			for (Patient p : queue)
			{
				if (p.getStatus() == PatientStatus.currently_unavailable || p.getStatus() == PatientStatus.unavailable)
				{
					if (indexes.get(0).intValue() > queue.indexOf(p))
					{
						Collections.swap(queue, indexes.get(0), queue.indexOf(p));
						break;
					}
				}
			}
			break;
		}
		case 2:
		{
			int swapped = 0;
			for (Patient p : queue)
			{
				if (p.getStatus() == PatientStatus.currently_unavailable || p.getStatus() == PatientStatus.unavailable)
				{
					for (int i = 0; i < 2; i++)
					{
						if (swapped == 2)
						{
							break;
						}
						if (indexes.get(i).intValue() > queue.indexOf(p))
						{
							Collections.swap(queue, indexes.get(i), queue.indexOf(p));
							swapped++;

						}
					}
				}
			}

		}
		}
	}

	public String representQueue()
	{
		StringBuilder sb = new StringBuilder();
		for (Patient p : queue)
		{
			sb.append(getSymbol(p));
		}
		return sb.toString();
	}

	/**
	 * Method for debugging, checking the queue composition
	 */
	public String getSymbol(Patient p)
	{
		String returnvalue = "";
		switch (p.getStatus())
		{
		case available:
			returnvalue = "AV-";
			break;
		case currently_unavailable:
			returnvalue = "CU-";
			break;
		case unavailable:
			returnvalue = "UN-";
			break;
		case under_treatment:
			returnvalue = "UT-";
			break;
		}
		return returnvalue;
	}

	// Add an element at the end of the queue
	// We suppose that one Patient can get only one treatment a day.
	public boolean offer(Patient patient)
	{
		if (queue.contains(patient))
		{
			return false;
		} else
		{
			queue.add(patient);
			sort();
			return true;
		}
	}

	// remove the top element from the list
	public Patient removeTopElement()
	{
		if (!queue.isEmpty())
		{
			Patient patient = queue.get(0);
			queue.remove(0);
			return patient;
		} else
		{
			return null;
		}
	}

	public boolean insertAtIndex(Patient patient, int spot)
	{
		if (queue.contains(patient))
		{
			return false;
		} else
		{
			queue.add(spot, patient);
			return true;
		}
	}

	// Directly connected to the MoveUp button in the admin user interface
	public void swapUp(Patient p)
	{
		if (queue.contains(p))
		{
			int index = queue.indexOf(p);
			if (index > 0)
			{
				Collections.swap(queue, index, index - 1);
			}
		}
	}

	// Directly connected to the MoveDown button in the admin user interface
	public void swapDown(Patient p)
	{
		if (queue.contains(p))
		{
			int index = queue.indexOf(p);
			if (index < (queue.size() - 1))
			{
				Collections.swap(queue, index, index + 1);
			}
		}
	}

	// For finding an element a concurrent hashmap would be ideal. But then the
	// ordering would be gone each time something
	// would be altered to the queue.
	public Patient find(Patient patient)
	{
		for (Patient p : queue)
		{
			if (p.getMedicalRecordId().equals(patient.getMedicalRecordId()))
			{
				return p;
			}
		}
		return null;
	}

	public void printList(CopyOnWriteArrayList<Patient> list)
	{
		for (Patient p : list)
		{
			System.out.println(p.toString());
		}
		System.out.println();
	}

	
	/**
	 *Removal of a patient from the list. We need to reset the machine that was treating this patient. 
	 */
	public boolean remove(Patient p)
	{
		int index = queue.indexOf(p);
		System.out.println("removing " + queue.get(index).getMedicalRecordId() + " from queue\n");
		if (index > -1 && index < queue.size())
		{
			String mrid = queue.get(index).getMedicalRecordId();
			for (Machine m : machines)
			{
				if (m.getCurrentPatient().equals(mrid))
				{
					System.out.println("Machine stopped and available...");
					m.reset();
				}
			}
			queue.remove(index);
			return true;
		} else
			return false;
	}

	// We are only interested in how the queue looks like
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (Patient p : queue)
		{
			sb.append(p.toString() + "\n");
		}
		return sb.toString();
	}

	
	/**
	 * The amount of machines and their treatment time determines the waiting times. So we need to know that when 
	 * a therapy session starts.
	 */
	public void addMachines()
	{
		machines = new Machine[settings.amountOfMachines];
		for (int i = 0; i < settings.amountOfMachines; i++)
		{
			machines[i] = new Machine(settings.treatmentTime);
		}
	}

	public int getLenght()
	{
		return queue.size();
	}

	public CopyOnWriteArrayList<Patient> getQueue()
	{
		return queue;
	}

	public void setQueue(CopyOnWriteArrayList<Patient> queue)
	{
		this.queue = queue;
	}

	public AdminSettings getSettings()
	{
		return settings;
	}

	public void setSettings(AdminSettings settings)
	{
		this.settings = settings;
	}

	public boolean isSessionStarted()
	{
		return sessionStarted;
	}

	public void setSessionStarted(boolean sessionStarted)
	{
		this.sessionStarted = sessionStarted;
	}

}
