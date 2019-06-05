package schoolregister.utils;

import java.sql.Date;
import java.util.Calendar;

public class Time {
    public static int dateToDayId(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static String dayOfWeekToString(int dayOfWeek) {
        if(dayOfWeek == 2)
            return "Monday";
        if(dayOfWeek == 3)
            return "Tuesday";
        if(dayOfWeek == 4)
            return "Wednesday";
        if(dayOfWeek == 5)
            return "Thursday";
        return "Friday";
    }

    public static String dateToDayString(Date date) {
        return dayOfWeekToString(dateToDayId(date));
    }
}
