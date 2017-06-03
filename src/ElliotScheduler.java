import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by lloyd on 2017-06-03.
 * Coding challenge for Elliot Technologies.
 */
public class ElliotScheduler {
    private static final SimpleDateFormat basicSDF = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws Exception {
        // Set up hash map for available time nodes.
        HashMap<String, AvailableLinkedNode> availableDays = new HashMap<String, AvailableLinkedNode>();
        GregorianCalendar cal = new GregorianCalendar();
        for (int i = 0; i < 7; i++) {
            availableDays.put(basicSDF.format(cal.getTime()), new AvailableLinkedNode(i));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Read file
        FileInputStream fis = new FileInputStream("calendar.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null) {
            // Parse the busy start and end times
            // User ID doesn't really matter in this challenge
            String[] data = line.split(", ");
            GregorianCalendar startDate = new GregorianCalendar();
            startDate.setTime(AvailableLinkedNode.parseSDF.parse(data[1]));
            GregorianCalendar endDate = new GregorianCalendar();
            endDate.setTime(AvailableLinkedNode.parseSDF.parse(data[2]));

            // Pass the busy period to the appropriate available day
            // We only care about days for the next week
            // Assumes both start and end date are on the same day
            String date = basicSDF.format(startDate.getTime());
            if (availableDays.containsKey(date)) {
                availableDays.get(date).addBusyTime(startDate, endDate);
            }
        }
        br.close();

        // Find the largest available period
        AvailableLinkedNode largestAvailable = null;
        for (AvailableLinkedNode available : availableDays.values()) {
            AvailableLinkedNode nextNode = available;
            while (nextNode != null) {
                if (largestAvailable == null ||
                        nextNode.getAvailableTimeInSeconds() > largestAvailable.getAvailableTimeInSeconds()) {
                    largestAvailable = nextNode;
                }
                nextNode = nextNode.next;
            }
        }

        // Print results
        if (largestAvailable != null) {
            System.out.println(largestAvailable.toString());
        }
    }
}
