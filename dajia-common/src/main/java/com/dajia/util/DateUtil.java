package com.dajia.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by huhaonan on 2016/10/26.
 */
public class DateUtil {

    public final static Long todayMorning() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public final static Long tomorrowMorning() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTimeInMillis();
    }

    public static void main(String[] args) {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(todayMorning()));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tomorrowMorning()));
        System.out.println(todayMorning());
        System.out.println(tomorrowMorning());
    }
}
