package afr.iterson.impatient.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import com.google.common.collect.Lists;

import afr.iterson.impatient.model.CustomComparator;
import afr.iterson.impatient.model.ImpatientUser;
import afr.iterson.impatient.model.Patient;
import afr.iterson.impatient.model.PatientStatus;
import afr.iterson.impatient.model.StatusMessage;
import afr.iterson.impatient.model.WaitingQueue;
import afr.iterson.impatient.model.WaitingQueueStatus;
import afr.iterson.impatient.repository.ImpatientUserRepository;
import afr.iterson.impatient.repository.MongoPatientRepo;
import afr.iterson.impatient.repository.MongoWaitingQueueRepo;
import afr.iterson.impatient.utils.Utils;

public class AdminControllerOps implements AdminSvcApi
{

	private static final Logger logger = LoggerFactory.getLogger(AdminControllerOps.class);

	@Autowired
	private MongoTemplate template;

	@Autowired
	private MongoPatientRepo patientRepo;

	@Autowired
	private MongoWaitingQueueRepo waitingQueueRepo;

	@Autowired
	private WaitingQueue queue;

	@Autowired
	private ImpatientUserRepository impatientUserRepo;

	/**
	 * Returns the list of patients sorted by appointmentdate
	 */
	@Override
	public Collection<Patient> getPatientList()
	{
		logger.debug("getPatientList()");
		List<Patient> list = Lists.newArrayList(patientRepo.findAll());
		Collections.sort(list, new CustomComparator());
		return list;
	}

	/**
	 * Just adding a patient to the Patient repository and to the ImpatientUsers
	 * repository; The returned object contains the new credentials. TODO :
	 * adding functionality to add a new patient that has already a MRID
	 */
	@Override
	public Patient addNewPatient(Patient patient)
	{
		logger.debug("Adding patient {}", patient);
		if (patientRepo.findBymedicalRecordId(patient.getMedicalRecordId()) == null)
		{
			patient.setAccessCode(Patient.makeAccessCode());
			patient.setMedicalRecordId(Patient.makeMedicalRecordId());
			patientRepo.save(patient);
			impatientUserRepo.save(new ImpatientUser(patient.getMedicalRecordId(), patient.getAccessCode(), true,
					"ROLE_USER"));
			return patientRepo.findBymedicalRecordId(patient.getMedicalRecordId());
		} else
		{
			logger.error("Adding patient {} , but duplicate recordid found", patient);

			return null;
		}
	}

	@Override
	public Patient editPatient(Patient patient)
	{
		logger.debug("editPatient() {}", patient);
		Patient pq = queue.find(patient);
		if(pq != null)
		{
			pq.setAppointmentDate(patient.getAppointmentDate());
		}
		Patient p = patientRepo.findBymedicalRecordId(patient.getMedicalRecordId());
		if (p != null)
		{
			p.setAppointmentDate(patient.getAppointmentDate());
			p.setBirthDate(patient.getBirthDate());
			p.setFirstName(patient.getFirstName());
			p.setLastName(patient.getLastName());
			p.setGender(patient.getGender());
			p.setMedicalRecordId(patient.getMedicalRecordId());
			patientRepo.save(p);
			return p;
		} else
		{
			logger.error("Editing patient {} , but not yet subscribed", patient);
			return null;
		}
	}

	@Override
	public Patient findByMedicalRecordId(String medicalRecordId)
	{

		Patient p = patientRepo.findBymedicalRecordId(medicalRecordId);
		if (p != null)
		{
			logger.debug("findByMedicalRecordId() {} : {}", medicalRecordId, p);
			return p;
		} else
		{
			logger.error("findByMedicalRecordId() {} ,but nothing found.", medicalRecordId);
			return null;
		}
	}

	/**
	 * returns the default list for today; since we decide to have millis in our
	 * date fields we first convert today to an interval between the lowest and
	 * the highest millisecond value of this day. We convert these longs to
	 * Strings to be able to query the db. BasicQuery is a class that cooperates
	 * with MongoTemplate to formulate a query. Now the session is started, a
	 * thread runs inside the waitingqueue to register the passage of time. This
	 * starts when treatments commence.
	 */
	@Override
	public Collection<Patient> startSession()
	{
		if (!queue.isSessionStarted())
		{
			// find all the patients for today
			System.out.println("Starting a session...");
			Date today = new Date();
			logger.debug("startSession() for {} ", today.toString());
			Calendar cal = Calendar.getInstance();
			long millisupperboundL = Utils.getEndOfDay(today, cal);
			long millislowerboundL = Utils.getBeginningOfDay(today, cal);
			BasicQuery query = new BasicQuery("{appointmentDate : {$lt: " + millisupperboundL + ",$gt:"
					+ millislowerboundL + "}}");
			Collection<Patient> sessionlist = template.find(query, Patient.class);
			// Initialize the waiting queue
			queue.getQueue().clear();
			for (Patient p : sessionlist)
			{
				queue.offer(p);
			}
			queue.setSessionStarted(true);
			queue.startThread();
			persist(queue);
		}
		System.out.println(queue.toString());
		return queue.getQueue();
	}

	@Override
	public StatusMessage stopSession()
	{
		queue.setSessionStarted(false);
		return new StatusMessage(StatusMessage.messages.session_stopped);
	}

	/***
	 * Returns the list of patients that are under treatment in a particular
	 * period
	 */
	@Override
	public Collection<Patient> getSessionList(String millislowerbound, String millisupperbound)
	{
		BasicQuery query = new BasicQuery("{appointmentDate : {$gt :  " + millislowerbound
				+ " } , appointmentDate : {$lt : " + millisupperbound + "}}");
		return template.find(query, Patient.class);
	}

	@Override
	public Collection<Patient> moveUp(Patient patient)
	{
		logger.debug("moveUp() {}", patient);
		queue.swapUp(patient);
		persist(queue);
		return queue.getQueue();
	}

	@Override
	public Collection<Patient> moveDown(Patient patient)
	{
		logger.debug("moveDown() {}", patient);
		queue.swapDown(patient);
		persist(queue);
		return queue.getQueue();
	}

	// Changing a patients status from/to
	// "Available/Unavailable/Currently_Unavailable/Under_Treatment"
	// and writing it to the queue.
	@Override
	public Collection<Patient> changeStatus(Patient patient)
	{
		System.out.println("changing patients " + patient.getMedicalRecordId() + " status...\n");
		Patient p = queue.find(patient);
		if (p != null)
		{
			System.out.println("found patient " + p.getFirstName() + " " + p.getLastName() + " in the queue...\n");
			p.setStatus(patient.getStatus());

			// when a patient is under treatment the queue must know to start a
			// 15 minute countdown timer.
			queue.changeStatusToAddMachine(p);
			persist(queue);
		}
		return queue.getQueue();
	}

	// Deleting a Patient from the queue and removing the appointment from the
	// list in the PatientRepository.
	@Override
	public Collection<Patient> deleteFromSession(Patient patient)
	{
		System.out.println("deleting " + patient.getMedicalRecordId() + " from session");
		Patient p = queue.find(patient);
		queue.remove(p);
		persist(queue);
		Patient p2 = patientRepo.findBymedicalRecordId(patient.getMedicalRecordId());
		Date date = new Date();
		if(patient.getAppointmentDate() < date.getTime())
		{
			p2.setAppointmentDate(0);
		}
		patientRepo.save(p2);
		return queue.getQueue();
	}

	// Launch a separate thread to persist
	public void persist(final WaitingQueue q)
	{
		logger.debug("persisting  present queue", q.toString());
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				waitingQueueRepo.deleteAll();
				waitingQueueRepo.save(q);
			}
		};
		new Thread(runnable).start();
	}

	// Getting a single patient from the queue
	@Override
	public Patient getFromQueue(String medicalrecordid)
	{
		return queue.find(patientRepo.findBymedicalRecordId(medicalrecordid));
	}

	/**
	 * Changing the treatment time and the amount of machines; The session has to be restarted for this. Unfortunately
	 * I still have no idea how to effectively cope with Threading in this context; one cannot a Thread as global to a class, 
	 * so to interrupt a thread is for me for the time being impossible. Tips would be appreciated.
	 */
	@Override
	public StatusMessage changeSettings(int howMuchMachines, int treatmentTime)
	{
		stopSession();
		queue.getSettings().setAmountOfMachines(howMuchMachines);
		queue.getSettings().setTreatmentTime(treatmentTime);
		try
		{
			Thread.sleep(WaitingQueue.SLEEPING_TIME);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		startSession();
		return new StatusMessage(StatusMessage.messages.settings_changed);
	}

	// Insert a patient at a given point in the queue
	@Override
	public Collection<Patient> insertInSession(Patient patient, int position)
	{
		queue.insertAtIndex(patient, position);
		return queue.getQueue();
	}

	// Standard method that is adressed by the polling service from the admin
	// client app
	@Override
	public Collection<Patient> pollSession()
	{
		return queue.getQueue();
	}

	@Override
	public StatusMessage checkUser()
	{
		// returns right away from the controller
		return null;
	}

}