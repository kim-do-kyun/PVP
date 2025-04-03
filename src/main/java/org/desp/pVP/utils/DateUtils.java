package org.desp.pVP.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String getCurrentTime() {
        Date now = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = "-";
        dateTime = dateFormatter.format(now.getTime());
        return dateTime;
    }

    public static String getCurrentDate() {
        Date now = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = "-";
        dateTime = dateFormatter.format(now.getTime());
        return dateTime;
    }
}
