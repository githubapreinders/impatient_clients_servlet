package afr.iterson.impatient_admin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.aidl.PatientStatus;

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

	private static long SLEEPING_TIME = 10000;

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
		shortestWaitingTimes = new long[settings.getAmountOfMachines()];
		addMachines();
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
			do
			{
				adaptWaitingTimes();
				System.out.println("checking the queue\n");
				try
				{
					Thread.sleep(SLEEPING_TIME);
				} catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			} while (sessionStarted == true);
			System.out.println("exiting queue monitoring, stopping thread...");
			System.out.println("Closing down machines and resetting to the defaults...\n");
			for(Machine m : machines)
			{
				m.reset();
			}
			Thread.currentThread().interrupt();
		}

	}

	public void startThread()
	{
		Thread t = new Thread(new QueueAdapter());
		t.start();
	}

	public void changeStatusToAddMachine(Patient p)
	{
		if (p.getStatus() == PatientStatus.under_treatment)
		{
			System.out.println("adding machines to the session...\n");
			p.setWaitingTime(0);
			for (int i = 0; i < machines.length; i++)
			{
				if (!(machines[i].isRunning()))
				{
					System.out.println("machine started for patient...\n");
					machines[i].start();
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
		//Check how many machines are running; the running machines remaining times are put in an array.
		for (int i = 0; i < shortestWaitingTimes.length; i++)
		{
			if (machines[i].isRunning())
			{
				shortestWaitingTimes[i] = machines[i].getRemainingTreatmentTime();
				length++;
			}
		}

		int index = 0;
		int awarded = 0;

		for (Patient patient : queue)
		{

			switch (patient.getStatus())
			{
			case available:
			{
				patient.setWaitingTime(shortestWaitingTimes[index] + settings.treatmentTime * awarded);
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

				patient.setWaitingTime(shortestWaitingTimes[index] + settings.treatmentTime * awarded);
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
				break;
			}
			case under_treatment:
			{
				break;
			}
			default:
				break;

			}

		}
		printList(queue);
	}

	// We move the first two available patients to the front of the queue to
	// make sure that there are always two persons
	// to call.
	public void moveFirstAvailablePatientsToFront()
	{
		System.out.println("Waiting Queue : move first available patients forward\n");
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
			System.out.println("indexes size is " + indexes.size() + " should be 2 \n");
			int q = 0;
			for (Patient p : queue)
			{
				if (p.getStatus() == PatientStatus.currently_unavailable || p.getStatus() == PatientStatus.unavailable)
				{
					if (indexes.get(q).intValue() > queue.indexOf(p))
					{
						System.out.println("swapping " + indexes.get(q) + "and " + queue.indexOf(p));
						Collections.swap(queue, indexes.get(q), queue.indexOf(p));
						q++;
						if (q == 2)
						{
							break;
						}
					}
				}
			}

		}

		}

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
		if (queue.contains(patient) || spot > (queue.size() - 1) || spot < 0)
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
		System.out.println("moving " + p.getMedicalRecordId() + " up in the queue\n");

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
		System.out.println("moving " + p.getMedicalRecordId() + " down in the queue\n");
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
	// ordering would be gone each time somethin
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

	public boolean remove(int index)
	{
		System.out.println("removing " + queue.get(index).getMedicalRecordId() + " from queue\n");
		if (index > -1 && index < queue.size())
		{
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

	public void addMachines()
	{
		System.out.println("adding machines to the session...\n");
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
