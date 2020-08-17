package Tests;

/*
 * Class for getting the length of time to complete an individual test
 */

public class MyLocalTime {

	private boolean initStart; // Tracks if startEpoch has been set
	private boolean initEnd;   // Tracks if endEpoch has been set
	private long startEpoch;   // Unix timestamp for start time
	private long endEpoch;     // Unix timestamp for end time

	private final long MILLIS_IN_HOUR = 3600000; // # of milliseconds per hour
	private final long MILLIS_IN_MIN = 60000;    // # of milliseconds per minute
	private final long MILLIS_IN_SEC = 1000;     // # of milliseconds per second

	/* Constructor */
	public MyLocalTime() {
		initStart = false;
		initEnd = false;
	}
	
	/*
	 * Initializes the start epoch to now if it has not been initialized already
	 */
	public void initStartEpoch() {
		if (!initStart) {
			startEpoch = System.currentTimeMillis();
			initStart = true;
		}
	}

	/*
	 * Initializes the ending epoch to now if it has bot been initialized already
	 */
	public void initEndEpoch() {
		if (!initEnd) {
			endEpoch = System.currentTimeMillis();
			initEnd = true;
		}
	}

	/* Getters w/ intitialization checks */
	public long getStartEpoch() {
		if (!initStart)
			initStartEpoch();
		return startEpoch;
	}

	public long getEndEpoch() {
		if (!initEnd)
			initEndEpoch();
		return endEpoch;
	}

	/*
	 * Returns the amount of time between the starting and ending epochs
	 */
	public String getTimeLength() {
		// Check for initialization
		if (!initStart)
			initStartEpoch();
		if (!initEnd)
			initEndEpoch();
		
		// Calculate total # of milliseconds between start and end
		long millis = Math.abs(endEpoch - startEpoch);

		// Calculates # of hours passed, and removes that many hours from millis
		int hours = Math.toIntExact(millis / MILLIS_IN_HOUR);
		millis -= (long) hours * MILLIS_IN_HOUR;

		// Calculates # of minutes passed, and removes that many minutes from millis
		long minutes = Math.toIntExact(millis / MILLIS_IN_MIN);
		millis -= (long) minutes * MILLIS_IN_MIN;

		// Calculates # of seconds passed, and removes that many from millis
		long seconds = Math.toIntExact(millis / MILLIS_IN_SEC);
		millis -= (long) seconds * MILLIS_IN_SEC;

		// Returns time passed
		return hours + "h " + minutes + "m " + seconds + "s " + millis + "ms";
	}
}
