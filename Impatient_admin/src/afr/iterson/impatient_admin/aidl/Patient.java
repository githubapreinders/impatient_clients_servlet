package afr.iterson.impatient_admin.aidl;

import java.util.Random;

import afr.iterson.impatient_admin.utils.Utils;
import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable
{

	private String gender;
	private String firstName;
	private String lastName;
	private String medicalRecordId;
	private boolean checkedIn;
	private boolean notified;
	private String accessCode;
	private PatientStatus status;

	// These dates are represented as Longs with Calendar.getTimeMillis() or
	// Date().getTimeMillis()
	private long birthDate;
	private long appointmentDate;

	// will only be used after a call from a Patient app
	private long waitingTime;

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Patient createFromParcel(Parcel in)
		{
			return new Patient(in);
		}

		public Patient[] newArray(int size)
		{
			return new Patient[size];
		}
	};

	public  Patient(Parcel in)
	{
		gender = in.readString();
		firstName = in.readString();
		lastName = in.readString();
		medicalRecordId = in.readString();
		checkedIn = (in.readInt() == 0) ? false : true;
		notified = (in.readInt() == 0) ? false : true;
		accessCode = in.readString();
		try
		{
			status = PatientStatus.valueOf(in.readString());
		} catch (IllegalArgumentException e)
		{
			status = PatientStatus.unavailable;
		}
		birthDate = in.readLong();
		appointmentDate = in.readLong();
		waitingTime = in.readLong();
	}

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
		this.status = PatientStatus.unavailable;
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
		this.status = PatientStatus.unavailable;
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
		this.status = PatientStatus.unavailable;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(gender);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(medicalRecordId);
		dest.writeInt(checkedIn ? 1 : 0);
		dest.writeInt(notified ? 1 : 0);
		dest.writeString(accessCode);
		dest.writeString((status == null) ? "" : status.name());
		dest.writeLong(birthDate);
		dest.writeLong(appointmentDate);
		dest.writeLong(waitingTime);
	}

	
	public String makeFullName()
	{
		return this.gender + " " + this.firstName + " " + this.lastName;
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

	
	
	@Override
	public String toString()
	{
		// return this.gender + " " + this.firstName + "\t" + this.lastName
		// + " \tMID:" + this.medicalRecordId
		// + " \tAppointment:"+
		// Utils.convertTimeMillisToReadableFormat(this.appointmentDate)
		// + " \tStatus:" + this.status
		// + " \tWaiting Time:" + this.getWaitingTime() ;

		return String.format("%4s %-25s %-7s Appointment: %-20s Status: %-22s Waiting time %-12d BirthDate: %-20s AccesCode: %5s" , this.gender,
				this.firstName + " " + this.lastName, this.medicalRecordId,
				Utils.convertTimeMillisToReadableFormat(this.appointmentDate), this.status, this.waitingTime, this.birthDate, this.accessCode);
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
