package ar.fi.uba.jobify.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by smpiano on 9/28/16.
 */
public class DateUtils {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final SimpleDateFormat SHORT_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SHORT_FORMATTER_ARG_1 = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat SHORT_FORMATTER_ARG_2 = new SimpleDateFormat("dd/MM/yyyy");

    private static Date parseDate(String dateStr, SimpleDateFormat format) {
        Date d = null;
        try {
            if (dateStr != null) {
                d = format.parse(dateStr);
            }
        } catch (ParseException pe) {
            Log.e("parse_date_error","Error tratando de parsear fecha "+dateStr,pe);
        }
        return d;
    }

    public static Date parseDate(String dateStr) {
        return parseDate(dateStr,FORMATTER);
    }

    public static Date parseShortDate(String dateStr) {
        return parseDate(dateStr,SHORT_FORMATTER);
    }

    public static Date parseShortDateArg1(String dateStr) { return parseDate(dateStr,SHORT_FORMATTER_ARG_1); }

    public static String formatShortDateArg1(Date date) {
        return SHORT_FORMATTER_ARG_1.format(date);
    }

    public static Date parseShortDateArg2(String dateStr) { return parseDate(dateStr,SHORT_FORMATTER_ARG_2); }

    public static String formatShortDateArg2(Date date) {
        return SHORT_FORMATTER_ARG_2.format(date);
    }

    public static String formatShortDate(Date date) {
        return SHORT_FORMATTER.format(date);
    }

    public static String formatDate(Date date) {
        return FORMATTER.format(date);
    }

    public static Integer getEdad(Date birthday) {
        Calendar c = Calendar.getInstance();
        long timeBetween = c.getTime().getTime() - birthday.getTime();
        double yearsBetween = timeBetween / 3.156e+10;
        return (int) Math.floor(yearsBetween);
    }
}
