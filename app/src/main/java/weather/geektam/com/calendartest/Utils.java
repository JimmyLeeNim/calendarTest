package weather.geektam.com.calendartest;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 */
public class Utils {

    private static Context sContext;

    private static Dimension sWindowSizeInPx;

    public static String secondsToHoursMinuts(long seconds) {
        int hour = (int) (seconds / 3600);
        int min = (int) (seconds % 3600) / 60;
        int sec = (int) (seconds % 3600) % 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
    }

    public static String appendWithComma(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String s = null;

        for (int i = 0; i < stringList.size(); i++) {
            s = stringList.get(i);
            result.append(s);
            if (i < stringList.size() - 1) {
                result.append(", ");
            }
        }

        return result.toString();
    }

    private static final SimpleDateFormat EDITORIAL_PROGRAMME_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);

    private static final SimpleDateFormat CATCHUP_DATE_FORMAT =
//            new SimpleDateFormat("HH:mm:ss", Locale.UK);
            new SimpleDateFormat("HH:mm:ss", Locale.UK);

    private static long convertToMidnightTime(int dayOffset) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, dayOffset);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static String getCatchupProgrammeStartTime(int dayOffset) {
        return EDITORIAL_PROGRAMME_DATE_FORMAT.format(convertToGMT(convertToMidnightTime(-dayOffset + 1)));
    }

    public static String getCatchupProgrammeEndTime(int dayOffset) {
        return EDITORIAL_PROGRAMME_DATE_FORMAT.format((convertToGMT(convertToMidnightTime(-dayOffset))));
    }

    private static long convertToGMT(long time) {
        return time - Calendar.getInstance().getTimeZone().getRawOffset();
    }

    private static Date convertToLocalTime(Date date) {
        return date == null ? null : new Date(date.getTime() + Calendar.getInstance().getTimeZone().getRawOffset());
    }

    public static String convertToCatchupDate(@NonNull String date) {
        Date convertedDate = null;
        try {
            convertedDate = convertToLocalTime(EDITORIAL_PROGRAMME_DATE_FORMAT.parse(date));
        } catch (Exception e) {
//            Log.e(TAG, e);
        }
        return convertedDate == null ? null : CATCHUP_DATE_FORMAT.format(convertedDate);
    }

    /*
    * 2016.04.25 kjh
    * milliseconds to Date(CATCHUP_DATE_FORMAT)
    * */
    public static String convertLivetvDate(@NonNull Long milliseconds) {
        return CATCHUP_DATE_FORMAT.format(milliseconds);
    }

    public static class Dimension {
        public final int width;
        public final int height;

        Dimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public enum TenseType {
        PAST,
        PRESENT,
        FUTURE
    }

    public static TenseType getContentTense(long startTimeMillis, long endTimeMillis) {
        final Calendar now = Calendar.getInstance();
        if (startTimeMillis <= now.getTimeInMillis() && endTimeMillis > now.getTimeInMillis()) {
            return TenseType.PRESENT;
        } else if (startTimeMillis > now.getTimeInMillis()) {
            return TenseType.FUTURE;
        } else {
            return TenseType.PAST;
        }
    }

    public static boolean isSameDay(@NonNull Calendar c1, @NonNull Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }


    public static boolean isLargeScreen(Context context) {
        if (context == null) {
            return false;
        }
        int screenLayout = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return true;
        }
        return false;
    }

    public static void keepScreenOnOff(Activity activity, boolean on) {
        if (activity != null) {
            int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            if (on) {
                activity.getWindow().addFlags(flag);
            } else {
                activity.getWindow().clearFlags(flag);
            }
        }
    }

    public static class InetAddressComparator implements Comparator<InetAddress> {
        @Override
        public int compare(InetAddress lhs, InetAddress rhs) {
            byte[] a1 = lhs.getAddress();
            byte[] a2 = rhs.getAddress();

            if (a1.length != a2.length) {
                return a1.length > a2.length ? 1 : -1;
            }

            for (int i = 0; i < a1.length; i++) {
                if (a1[i] != a2[i]) {
                    return ((a1[i] & 0x00ff) > (a2[i] & 0x00ff)) ? 1 : -1;
                }
            }

            return 0;
        }
    }
}
