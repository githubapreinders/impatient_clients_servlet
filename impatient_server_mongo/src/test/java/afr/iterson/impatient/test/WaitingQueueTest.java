package afr.iterson.impatient.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.junit.Test;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import afr.iterson.impatient.controller.AdminSvcApi;
import afr.iterson.impatient.controller.PatientSvcApi;
import afr.iterson.impatient.controller.SecuredRestBuilder;
import afr.iterson.impatient.controller.SecuredRestException;
import afr.iterson.impatient.model.Machine;
import afr.iterson.impatient.model.Patient;
import afr.iterson.impatient.model.PatientStatus;
import afr.iterson.impatient.model.StatusMessage;
import afr.iterson.impatient.model.WaitingQueue;
import afr.iterson.impatient.utils.Utils;

public class WaitingQueueTest
{
	private final String PATIENTUSERNAME = "9881MU";
	private final String PASSWORD2 = "PM8Y";

	private final String ADMINUSERNAME = "jules";
	private final String PASSWORD = "pass";
	private final String CLIENT_ID = "mobile";

	private final String TEST_URL8443 = "https://localhost:8443";
	private final String TEST_URL8080 = "http://localhost:8080";

	// Has admin rights
	private AdminSvcApi impatientAdminService1 = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL8443 + AdminSvcApi.TOKEN_PATH).setUsername(ADMINUSERNAME).setPassword(PASSWORD)
			.setClientId(CLIENT_ID).setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setLogLevel(LogLevel.NONE).setEndpoint(TEST_URL8443).build().create(AdminSvcApi.class);
	// Has only user rights
	private AdminSvcApi impatientIncorrectAdminService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL8443 + AdminSvcApi.TOKEN_PATH).setUsername(PATIENTUSERNAME)
			.setPassword(PASSWORD2).setClientId(CLIENT_ID).setLogLevel(LogLevel.NONE)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient())).setEndpoint(TEST_URL8443).build()
			.create(AdminSvcApi.class);

	// Incorrect credentials
	private AdminSvcApi impatientIncorrectAdminService2 = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL8443 + AdminSvcApi.TOKEN_PATH).setUsername(UUID.randomUUID().toString())
			.setPassword(UUID.randomUUID().toString()).setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient())).setEndpoint(TEST_URL8443)
			.setLogLevel(LogLevel.FULL).build().create(AdminSvcApi.class);

	// Unsafe connection via port 8080
	private AdminSvcApi insecureImpatientService = new RestAdapter.Builder().setEndpoint(TEST_URL8080)
			.setLogLevel(LogLevel.FULL).build().create(AdminSvcApi.class);

	// Patient with good credentials
	private PatientSvcApi impatientPatientService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL8443 + PatientSvcApi.TOKEN_PATH).setUsername(PATIENTUSERNAME)
			.setPassword(PASSWORD2).setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient())).setLogLevel(LogLevel.NONE)
			.setEndpoint(TEST_URL8443).build().create(PatientSvcApi.class);

	// Patient with incorrect credentials
	private PatientSvcApi impatientIncorrectPatientService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL8443 + PatientSvcApi.TOKEN_PATH).setUsername(UUID.randomUUID().toString())
			.setPassword(UUID.randomUUID().toString()).setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient())).setLogLevel(LogLevel.NONE)
			.setEndpoint(TEST_URL8443).build().create(PatientSvcApi.class);

	static WaitingQueue queue = new WaitingQueue();

	
	
	
	@Test
	public void checkIfPatientCanChangeStatus()
	{
		impatientAdminService1.startSession();
		Patient p = impatientPatientService.getStatus(PATIENTUSERNAME);
		p.setStatus(PatientStatus.available);
		impatientPatientService.changePatientStatus(p);
	}
	
	
	// Convenience method to stop the threads after the client crashes
	@Test
	public void stopSession()
	{
		impatientAdminService1.stopSession();
	}

	@Test
	public void checkIfPatientWithChangedStatusIsMovedToTheFront()
	{
		ArrayList<Patient> sessionlist = new ArrayList<Patient>(impatientAdminService1.startSession());
		sleep();
		sessionlist.get(5).setStatus(PatientStatus.under_treatment);
		impatientAdminService1.changeStatus(sessionlist.get(5));
		
	}
	
	
	
	/**
	 * Check queue behaviour : The queue should patients being put under treatment to the front, as well as
	 * two available paitients to follow after. 
	 */
	@Test
	public void setTwoPatientsAvailableAndLetThemBeMovedForward()
	{
		ArrayList<Patient> sessionlist = new ArrayList<Patient>(impatientAdminService1.startSession());
		impatientAdminService1.changeSettings(1, 15);
		sleep();
		sessionlist.get(5).setStatus(PatientStatus.available);
		impatientAdminService1.changeStatus(sessionlist.get(5));
		sessionlist.get(3).setStatus(PatientStatus.available);
		impatientAdminService1.changeStatus(sessionlist.get(3));
		sleep();
		sessionlist = new ArrayList<Patient>(impatientAdminService1.pollSession());
		sessionlist.get(7).setStatus(PatientStatus.under_treatment);
		impatientAdminService1.changeStatus(sessionlist.get(7));
		sleep();
		sessionlist = new ArrayList<Patient>(impatientAdminService1.pollSession());
		sessionlist.get(5).setStatus(PatientStatus.available);
		impatientAdminService1.changeStatus(sessionlist.get(5));
		sleep();
		sessionlist = new ArrayList<Patient>(impatientAdminService1.pollSession());
		assertEquals(PatientStatus.under_treatment,sessionlist.get(0).getStatus());
		assertEquals(PatientStatus.available,sessionlist.get(1).getStatus());
		assertEquals(PatientStatus.available,sessionlist.get(2).getStatus());
		impatientAdminService1.stopSession();

	}
	
	
	
	//Method that checks if Oauth has distinguished between admins and patients
	@Test
	public void checkUser()
	{
		StatusMessage s1 = impatientAdminService1.checkUser();
		assertEquals(true, s1.getMessage() == StatusMessage.messages.admin);
		AdminSvcApi service = new SecuredRestBuilder().setLoginEndpoint(TEST_URL8443 + AdminSvcApi.TOKEN_PATH)
				.setUsername(PATIENTUSERNAME).setPassword(PASSWORD2).setClientId(CLIENT_ID).setLogLevel(LogLevel.NONE)
				.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient())).setEndpoint(TEST_URL8443).build()
				.create(AdminSvcApi.class);
		StatusMessage s2 = service.checkUser();
		assertEquals(true, s2.getMessage() == StatusMessage.messages.patient);
	}

	@Test
	public void testAccessDeniedWithIncorrectCredentials() throws Exception
	{
		try
		{
			impatientIncorrectAdminService.addNewPatient(Utils.makeRandomPatient());
			fail("The server should have prevented the client from adding a video"
					+ " because it presented invalid client/user credentials");
		} catch (RetrofitError e)
		{
			assert (e.getCause() instanceof SecuredRestException);
		}
		impatientAdminService1.stopSession();

	}

	
	
	
	//Check if patientlist is sorted and sessionlist is indeed for today.
	@Test
	public void compareSessionAndPatientLists()
	{
		ArrayList<Patient> sessionlist = new ArrayList<Patient>(impatientAdminService1.startSession());
		ArrayList<Patient> patientlist = new ArrayList<Patient>(impatientAdminService1.getPatientList());
		
		long appo=0l;
		for(Patient p : patientlist)
		{
			assertEquals(true,p.getAppointmentDate() >= appo );
			appo = p.getAppointmentDate();
		}
		assertEquals(true, sessionlist.size() <= patientlist.size());
		for (Patient p : sessionlist)
		{
			assertEquals(true, Utils.inTodaysBoundaries(p.getAppointmentDate()));
		}
		impatientAdminService1.stopSession();
	}

	@Test
	public void Check_If_PreAuthorize_Annotation_Works()
	{
		try
		{
			Patient p1 = impatientAdminService1.addNewPatient(Utils.makeRandomPatient());
			assertEquals(true, p1 != null);

		} catch (RetrofitError e)
		{
			assert (e.getCause() instanceof SecuredRestException);
		}
		try
		{
			Patient p = impatientIncorrectAdminService.addNewPatient(Utils.makeRandomPatient());
			fail("The server should have prevented the call because this client has" + "only ROLE_USER authority");
			assertEquals(true, p != null);
		} catch (RetrofitError e)
		{
			assert (e.getCause() instanceof SecuredRestException);
		}
	}

	@Test
	public void checkIfItWorks()
	{
		ArrayList<Patient> list = new ArrayList<Patient>(impatientAdminService1.pollSession());
		list.get(6).setStatus(PatientStatus.available);
		impatientAdminService1.changeStatus(list.get(6));
		list.get(7).setStatus(PatientStatus.available);
		impatientAdminService1.changeStatus(list.get(7));
		list.get(9).setStatus(PatientStatus.available);
		impatientAdminService1.changeStatus(list.get(9));
		list.get(0).setStatus(PatientStatus.under_treatment);
		impatientAdminService1.changeStatus(list.get(0));
	}
	
	
	
	public void sleep()
	{
		try
		{
			Thread.sleep(15000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void Check_If_New_Patient_Is_Added_And_Has_AccessCode()
	{
		Patient p = Utils.makeRandomPatient();
		p.setAccessCode(null);
		Patient returnedpatient = impatientAdminService1.addNewPatient(p);
		assertEquals(true, returnedpatient.getAccessCode() != null);
	}

	@Test
	public void CHECK_MOVEMENT_AROUND_THE_LIST()
	{
		ArrayList<Patient> sessionlist = new ArrayList<Patient>(impatientAdminService1.startSession());
		Patient p = sessionlist.get(sessionlist.size() - 1);
		ArrayList<Patient> sessionlist2 = new ArrayList<Patient>(impatientAdminService1.moveDown(p));
		Patient p1 = sessionlist2.get(sessionlist2.size() - 1);
		assertEquals(true, p.getMedicalRecordId().equals(p1.getMedicalRecordId()));
		impatientAdminService1.stopSession();
	}

	
	
	@Test
	public void CHECK_IF_PATIENT_GETS_WAITING_TIME()
	{
		ArrayList<Patient> daypatients = new ArrayList<Patient>(impatientAdminService1.startSession());
		Patient p = daypatients.get(0);
		// Patient ppp =
		// impatientSecuredService1.findByMedicalRecordId(p.getMedicalRecordId());

		PatientSvcApi service2 = new SecuredRestBuilder().setLoginEndpoint(TEST_URL8443 + PatientSvcApi.TOKEN_PATH)
				.setUsername(p.getMedicalRecordId()).setPassword(p.getAccessCode()).setClientId(CLIENT_ID)
				.setLogLevel(LogLevel.NONE).setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
				.setEndpoint(TEST_URL8443).build().create(PatientSvcApi.class);

		// Making sure we can log in with these credentials.
		assertEquals(true, service2 != null);

		try
		{
			p.setStatus(PatientStatus.available);
			p = service2.changePatientStatus(p);

			Patient p2 = impatientAdminService1.getFromQueue(p.getMedicalRecordId());
			assertEquals(true, p.getStatus() == p2.getStatus());

			impatientAdminService1.stopSession();
		} catch (RetrofitError e)
		{
			impatientAdminService1.stopSession();
			assert (e.getCause() instanceof SecuredRestException);
		} catch (Exception e)
		{
			impatientAdminService1.stopSession();
		}

	}

	public void printList(ArrayList<Patient> list)
	{
		for (Patient p : list)
		{
			System.out.println(p.toString());
		}
		System.out.println();
	}



}
