package afr.iterson.impatient.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import afr.iterson.impatient.model.Patient;


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
							.makeRandomAppointmentDate().getTimeInMillis(),Patient.makeAccessCode(), Utils.makeRandomBirthDate()
							.getTimeInMillis());
		case 1:
			return new Patient("mr", maleFirstNames[(int) (Math.random() * maleFirstNames.length)],
					lastNames[(int) (Math.random() * lastNames.length)], Patient.makeMedicalRecordId(), Utils
							.makeRandomAppointmentDate().getTimeInMillis(), Patient.makeAccessCode(),Utils.makeRandomBirthDate()
							.getTimeInMillis());
		default:
			return new Patient();

		}
	}

	public static GregorianCalendar makeAppointmentDateForToday()
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.YEAR, 2015);
		calendar.set(
				GregorianCalendar.DAY_OF_YEAR,Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 12);
		int minuteOfDay = (int) (Math.random() * 4) * 15;
		calendar.set(GregorianCalendar.MINUTE, minuteOfDay);
		return calendar;
	}
	
	
	public static GregorianCalendar makeRandomAppointmentDate()
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.YEAR, 2015);
		//int dayOfYear = randBetween(1, calendar.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
		int dayOfYear = randBetween(Calendar.getInstance().get(Calendar.DAY_OF_YEAR), Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 5);
		calendar.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
		int hourOfDay = randBetween(8, 17);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
		int minuteOfDay = (int) (Math.random() * 4) * 15;
		calendar.set(GregorianCalendar.MINUTE, minuteOfDay);
		return calendar;
	}

	public static GregorianCalendar makeRandomBirthDate()
	{
		GregorianCalendar calendar = new GregorianCalendar();

		int year = randBetween(1915, 1995);
		calendar.set(GregorianCalendar.YEAR, year);
		int dayOfYear = randBetween(1, calendar.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
		calendar.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
		return calendar;
	}

	public static int randBetween(int start, int end)
	{
		return start + (int) Math.round(Math.random() * (end - start));
	}

	// Json does not know a Date field; therefore we convert a calendar object
	// to its epoch value in millis.
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

	// The epoch values can easily be replaced by readable values.
	public static String convertTimeMillisToReadableFormat(long millis)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd YYYY HH:mm ");
		Date date = new Date(millis);
		return sdf.format(date);
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

}
