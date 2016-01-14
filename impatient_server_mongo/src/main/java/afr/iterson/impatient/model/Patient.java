package afr.iterson.impatient.model;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import afr.iterson.impatient.utils.Utils;

@Document(collection = "patients")
public class Patient
{

	@Id
	private String id;

	private String gender;
	private String firstName;
	private String lastName;
	private String medicalRecordId;
	private boolean checkedIn;
	private boolean notified;
	private String accessCode;
	private PatientStatus status = PatientStatus.unavailable;

	// These dates are represented as Longs with Calendar.getTimeMillis() or
	// Date().getTimeMillis()
	private long birthDate;
	private long appointmentDate;

	// will only be used after a call from a Patient app
	private long waitingTime;

	public Patient()
	{
	}

	public Patient(String gender, String firstname, String lastname, String medicalrecordid, long appointmentDate)
	{
		this.gender = gender;
		this.firstName = firstname;
		this.lastName = lastname;
		this.medicalRecordId = medicalrecordid;
		this.appointmentDate = appointmentDate;
	}

	public Patient(String gender, String firstname, String lastname, String medicalrecordid, long appointmentDate,
			String accessCode)
	{
		this.gender = gender;
		this.firstName = firstname;
		this.lastName = lastname;
		this.medicalRecordId = medicalrecordid;
		this.appointmentDate = appointmentDate;
		this.accessCode = accessCode;
	}

	public Patient(String gender, String firstname, String lastname, String medicalrecordid, long appointmentDate,
			String accessCode, long birthDate)
	{
		this.gender = gender;
		this.firstName = firstname;
		this.lastName = lastname;
		this.medicalRecordId = medicalrecordid;
		this.appointmentDate = appointmentDate;
		this.accessCode = accessCode;
		this.birthDate = birthDate;
	}

	// making a static pin code that functions as the password; this pin code is
	// supplied by an admin during subscription.
	public static String makeAccessCode()
	{
		char[] chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
		return makeRandomString(chars, 4);
	}

	public static String makeMedicalRecordId()
	{
		char[] chars = "1234567890".toCharArray();
		StringBuilder sb = new StringBuilder(makeRandomString(chars, 4));
		chars = "ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
		sb.append(makeRandomString(chars, 2));
		return sb.toString();
	}

	public static String makeRandomString(char[] chars, int lenght)
	{
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < lenght; i++)
		{
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	public PatientStatus getStatus()
	{
		return status;
	}

	public void setStatus(PatientStatus status)
	{
		this.status = status;
	}

	// We regard two patients as equal when they have the same medical record.
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Patient))
		{
			return false;
		}
		Patient otherPatient = (Patient) obj;
		return this.medicalRecordId.equals(otherPatient.medicalRecordId);
	}

	public static void main(String[] args)
	{
		ArrayList<Patient> patients = new ArrayList<Patient>();
		for(int i=0 ; i<5;i++)
		{
			patients.add(Utils.makeRandomPatient());
		}
		for(Patient p : patients)
		{
			System.out.println(p.toString());
		}
		
		
		
	}
	
	
	@Override
	public String toString()
	{
//		return this.gender + " " + this.firstName + "\t" + this.lastName 
//				+ " \tMID:" + this.medicalRecordId
//				+ " \tAppointment:"+ Utils.convertTimeMillisToReadableFormat(this.appointmentDate)
//				+ " \tStatus:" + this.status 
//				+ " \tWaiting Time:"	+ this.getWaitingTime() ;
		
		return String.format("%4s %-25s %-7s Appointment: %-20s Status: %-22s Waiting time %-12d", 
				this.gender,
				this.firstName + " " + this.lastName,
				this.medicalRecordId,
				Utils.convertTimeMillisToReadableFormat(this.appointmentDate),
				this.status,
				this.waitingTime
				);
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getMedicalRecordId()
	{
		return medicalRecordId;
	}

	public void setMedicalRecordId(String medicalRecordId)
	{
		this.medicalRecordId = medicalRecordId;
	}

	public boolean isCheckedIn()
	{
		return checkedIn;
	}

	public void setCheckedIn(boolean checkedIn)
	{
		this.checkedIn = checkedIn;
	}

	public long getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(long birthDate)
	{
		this.birthDate = birthDate;
	}

	public long getAppointmentDate()
	{
		return appointmentDate;
	}

	public void setAppointmentDate(long appointmentDate)
	{
		this.appointmentDate = appointmentDate;
	}

	public String getAccessCode()
	{
		return accessCode;
	}

	public void setAccessCode(String accessCode)
	{
		this.accessCode = accessCode;
	}

	public long getWaitingTime()
	{
		return waitingTime;
	}

	public void setWaitingTime(long waitingTime)
	{
		this.waitingTime = waitingTime;
	}

	public boolean isNotified()
	{
		return notified;
	}

	public void setNotified(boolean notified)
	{
		this.notified = notified;
	}
}
