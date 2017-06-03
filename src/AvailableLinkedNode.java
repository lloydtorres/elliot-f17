import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by lloyd on 2017-06-03.
 * Linked list-like structure for storing times free for all users.
 */
public class AvailableLinkedNode {
    public static final SimpleDateFormat parseSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    public AvailableLinkedNode next;

    public AvailableLinkedNode(int dateOffset) {
        startTime = new GregorianCalendar();
        endTime = new GregorianCalendar();

        // Add dateOffset to startTime's and endTime's day field
        // dateOffset represents the number of days from today
        startTime.add(Calendar.DATE, dateOffset);
        endTime.add(Calendar.DATE, dateOffset);

        // Assuming all users are free, their available meeting times are from 8am to 10pm
        startTime.set(Calendar.HOUR_OF_DAY, 8);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        endTime.set(Calendar.HOUR_OF_DAY, 22);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);
    }

    private AvailableLinkedNode(GregorianCalendar st, GregorianCalendar et, AvailableLinkedNode n) {
        startTime = st;
        endTime = et;
        next = n;
    }

    /**
     * Given the start and end time of a busy period, change the period of the available time.
     * @param busyStartTime
     * @param busyEndTime
     */
    public void addBusyTime(GregorianCalendar busyStartTime, GregorianCalendar busyEndTime) {
        // Case 1: Busy time is entirely within the current available time
        if (isAfterTime(startTime, busyStartTime) && !isAfterTime(endTime, busyEndTime)) {
            GregorianCalendar currentEndTime = endTime;
            AvailableLinkedNode currentNextNode = next;

            endTime = busyStartTime;
            next = new AvailableLinkedNode(busyEndTime, currentEndTime, currentNextNode);
            return;
        }
        // Case 2: Busy time only covers part of the current available time, before the current available end time
        else if (!isAfterTime(startTime, busyStartTime) &&
                isAfterTime(startTime, busyEndTime) &&
                !isAfterTime(endTime, busyEndTime)) {
            startTime = busyEndTime;
            return;
        }
        // Case 3: Busy time only covers part of the current available time, after the current available start time
        else if (isAfterTime(startTime, busyStartTime) &&
                !isAfterTime(endTime, busyStartTime) &&
                isAfterTime(endTime, busyEndTime)) {
            endTime = busyStartTime;
        }
        // Case 4: Current available time is entirely within busy time
        else if (!isAfterTime(startTime, busyStartTime) &&
                isAfterTime(endTime, busyEndTime)) {
            startTime = endTime;
        }

        // Pass on the busy time to the next node in case it's also covered
        if (next != null) {
            next.addBusyTime(busyStartTime, busyEndTime);
        }
    }

    /**
     * Returns true if the first time occurs after the second time; false otherwise.
     * @param time1
     * @param time2
     * @return
     */
    private boolean isAfterTime(GregorianCalendar time1, GregorianCalendar time2) {
        return time1.compareTo(time2) <= 0;
    }

    /**
     * Returns the period of the available time, in seconds.
     * @return
     */
    public int getAvailableTimeInSeconds() {
        return (endTime.get(Calendar.HOUR_OF_DAY) - startTime.get(Calendar.HOUR_OF_DAY)) * 60 * 60 +
                (endTime.get(Calendar.MINUTE) - startTime.get(Calendar.MINUTE)) * 60 +
                (endTime.get(Calendar.SECOND) - startTime.get(Calendar.SECOND));
    }

    @Override
    public String toString() {
        return parseSDF.format(startTime.getTime()) + ", " + parseSDF.format(endTime.getTime());
    }
}
