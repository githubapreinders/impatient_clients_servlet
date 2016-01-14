package afr.iterson.impatient_patient.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import afr.iterson.impatient_patient.aidl.Patient;
import android.content.Context;
import android.widget.Toast;

public class Utils
{
	public static Patient makeRandomPatient()
	{
		String[] femaleFirstNames = { "Gaby", "Patricia", "Olga", "Tamara", "Josephine", "Sue", "Karen", "Parvati",
				"Ooma", "Aigo", "Manuela", "Kerstin", "Alexandra", "Xiao-Ni", "Chihiro", "Freya", "Athena", "Carmela", "Julia","Patty"
				,"Hanna","Sarah","Josephine","Femke"};
		String[] maleFirstNames = { "Akira", "Lee", "Joshua", "Mordred", "Arthur", "Krishna", "Donovan", "Ryu", "Thor",
				"Bill", "Ap", "Sasha", "Marek", "Juan", "Heinz", "Loki","Henk","Piet","Klaas","Sgrna" };
		String[] lastNames = { "Patil", "Dumais", "Choi", "Hurley", "Reinders", "Eisenmann", "Kudo", "Saiki", "Diallo",
				"Cisse", "Asari-Dokubo", "Iwu", "Onwudiwe", "Chandra", "Rajawat", "Jones","van den Berg","Mascherano","Graafsma","de Boer",
				"Mandela","Asue-Koto", "Potter", "Ze Dong", "Yamamoto", "Gupta", "Solzjenitzin", "Turing"};

		int i = (int) (Math.random() * 2);

		switch (i)
		{
		case 0:
			return new Patient("ms", femaleFirstNames[(int) (Math.random() * femaleFirstNames.length)],
					lastNames[(int) (Math.random() * lastNames.length)], Patient.makeMedicalRecordId(), Utils
							.makeRandomAppointmentDate().getTimeInMillis(), Patient.makeAccessCode(), Utils
							.makeRandomBirthDate().getTimeInMillis());
		case 1:
			return new Patient("mr", maleFirstNames[(int) (Math.random() * maleFirstNames.length)],
					lastNames[(int) (Math.random() * lastNames.length)], Patient.makeMedicalRecordId(), Utils
							.makeRandomAppointmentDate().getTimeInMillis(), Patient.makeAccessCode(), Utils
							.makeRandomBirthDate().getTimeInMillis());
		default:
			return new Patient();

		}
	}

	/**
	 *Makes a random appointmentdate between Today and 5 days further. 
	 */
	public static GregorianCalendar makeRandomAppointmentDate()
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.YEAR, 2015);
		calendar.set(
				GregorianCalendar.DAY_OF_YEAR,
				randBetween(Calendar.getInstance().get(Calendar.DAY_OF_YEAR),
						Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 5));
		int hourOfDay = randBetween(8, 17);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
		int minuteOfDay = (int) (Math.random() * 4) * 15;
		calendar.set(GregorianCalendar.MINUTE, minuteOfDay);
		return calendar;
	}

	/**
	 *Makes a random birthdate between 1915 and 1995. 
	 */
	public static GregorianCalendar makeRandomBirthDate()
	{
		GregorianCalendar calendar = new GregorianCalendar();

		int year = randBetween(1915, 1995);
		calendar.set(GregorianCalendar.YEAR, year);
		int dayOfYear = randBetween(1, calendar.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
		calendar.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
		return calendar;
	}

	/**
	 *Gives a random digit between two int digits 
	 */
	public static int randBetween(int start, int end)
	{
		return start + (int) Math.round(Math.random() * (end - start));
	}

	/**
	 * Converts the int fields of a Calendar object into a String (not really useful)
	 */
	public static String makeDateStringAsMillis(int month, int day, int hour, int minutes)
	{
		try
		{
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Calendar.YEAR, 2015);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minutes);
			return String.valueOf(calendar.getTimeInMillis());
		} catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return "no date available";
		}
	}

	/**
	 * Convert ints from year, month and day into a long value.
	 */
	public static long makeDateAsMillis(int year, int month, int day)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		try
		{
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND,0);
		} catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		return calendar.getTimeInMillis();

	}

	/**
	 *Convert a time of day to the amount of millis after midnight 
	 */
	public static long makeTimeAsMillis(int hour, int minute)
	{
		long hours = hour*60*60*1000;
		long minutes = minute*60*1000;
		return (hours + minutes);
	}
	
	
	
	
	/**
	 *Converts the long value in the appointment time or birthdate to a string that
	 *can easier be accessed in a text search; Instead of having to type "dec. 25"
	 *one can just type "dec25" to retrieve the patients for 25 december. 
	 */
	public static String makeDateForSearchString(long date)
	{
		String datestring = convertTimeMillisToReadableFormat(date);
		StringBuilder sb = new StringBuilder(datestring);
		boolean space = true;
		do
		{
		if(sb.charAt(3) == '0'||sb.charAt(3) == '.'||sb.charAt(3) == ' ')
		{
			sb.deleteCharAt(3);
		}
		else
		{
			space = false;
		}
		}
		while(space);
		return sb.toString();
	}

	
	
	
	
	
	
	/**
	 * Returns a string like "AUG. 23 2015 19:30"
	 */
	public static String convertTimeMillisToReadableFormat(long millis)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm ");
		Date date = new Date(millis);
		return sdf.format(date);
	}

	/**
	 *Returns a string like  "SEP. 12 1939" 
	 */
	public static String convertTimeMillisToReadableFormatNoHouraAndMinutes(long millis)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy ");
		Date date = new Date(millis);
		return sdf.format(date);
	}

	
	/**
	 *Returns a string like  "10:30" 
	 */
	public static String convertTimeMillisToReadableFormatHoursAndMinutes(long millis)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ");
		Date date = new Date(millis);
		return sdf.format(date);
	}

	
	/**
	 *Returns a string like  "30" but also "3" (and not "03") 
	 */
	public static String convertTimeMillisToReadableFormatMinutes(long millis)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		Date date = new Date(millis);
		String returnstring = sdf.format(date);
		if (returnstring.substring(0, 1).equals("0"))
		{
			return returnstring.substring(1, 2);
		} else
		{
			return returnstring;
		}
	}

	public static long getEndOfDay(Date day, Calendar cal)
	{
		if (day == null)
			day = new Date();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		return cal.getTimeInMillis();
	}

	public static long getBeginningOfDay(Date day, Calendar cal)
	{
		if (day == null)
			day = new Date();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
		return cal.getTimeInMillis();
	}

	public static boolean inTodaysBoundaries(long date)
	{
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		long millisupperbound = Utils.getEndOfDay(today, cal);
		long millislowerbound = Utils.getBeginningOfDay(today, cal);
		return (date >= millislowerbound && date <= millisupperbound);

	}

	/**
	 * Show a toast message.
	 */
	public static void showToast(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

}
