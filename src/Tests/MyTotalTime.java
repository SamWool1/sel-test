package Tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Class for timestamps and calculating the total length of the test suites
 */

public class MyTotalTime {

	private static String myTime;             // Formatted timestamp for start time
	private static boolean initStart = false; // Tracks if start epoch has been initialized
	private static boolean initEnd = false;   // Tracks if end epoch has been initialized
	private static long startEpoch;           // Unix timestamp for start time
	private static long endEpoch;             // Unix timestamp for end time

	private static final long MILLIS_IN_HOUR = 3600000; // # of milliseconds in an hour
	private static final long MILLIS_IN_MIN = 60000;    // # of milliseconds in a minute
	private static final long MILLIS_IN_SEC = 1000;     // # of milliseconds in a second

	/*
	 * Initializes the start epoch to now, and creates the timestamp string
	 */
	public static void initStartEpoch() {
		if (!initStart) {
//			// Timestamp, tracks down to the second			
//			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH-mm-ss");
			
			// Timestamp, tracks down to the day
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("_yyyy-MM-dd");
			
			// Gets the current time
			startEpoch = System.currentTimeMillis();
			LocalDateTime now = LocalDateTime.now();
			myTime = dtf.format(now);
			initStart = true;
		}
	}

	/*
	 * Initializes the ending epoch to now
	 */
	public static void initEndEpoch() {
		if (!initEnd) {
			endEpoch = System.currentTimeMillis();
			initEnd = true;
		}
	}

	/* Getters */
	public static String get() {
		if (!initStart)
			initStartEpoch();
		return myTime;
	}

	public static long getStartEpoch() {
		if (!initStart)
			initStartEpoch();
		return startEpoch;
	}

	public static long getEndEpoch() {
		if (!initEnd)
			initEndEpoch();
		return endEpoch;
	}

	/*
	 * Calculates the amount of time passed from start to end
	 */
	public static String getTimeLength() {		
		// Initialization check
		if (!initStart)
			initStartEpoch();
		if (!initEnd)
			initEndEpoch();
		
		// Gets the total # of milliseconds between the start and the end
		long millis = Math.abs(endEpoch - startEpoch);

		// Calculates # of hours passed, and removes that many hours from millis
		int hours = Math.toIntExact(millis / MILLIS_IN_HOUR);
		millis -= (long) hours * MILLIS_IN_HOUR;

		// Calculates # of minutes passed, and removes that many minutes from millis
		long minutes = Math.toIntExact(millis / MILLIS_IN_MIN);
		millis -= (long) minutes * MILLIS_IN_MIN;

		// Calculates # of seconds passed, and removes that many seconds from millis
		long seconds = Math.toIntExact(millis / MILLIS_IN_SEC);
		millis -= (long) seconds * MILLIS_IN_SEC;

		// Returns time passed
		return hours + "h " + minutes + "m " + seconds + "s " + millis + "ms";
	}
}
