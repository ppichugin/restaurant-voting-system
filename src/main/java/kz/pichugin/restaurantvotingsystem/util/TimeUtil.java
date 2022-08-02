package kz.pichugin.restaurantvotingsystem.util;

import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeUtil {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static LocalTime timeLimit = LocalTime.of(11, 0);

    public static LocalTime getLimit() {
        return timeLimit;
    }

    @Profile("test")
    public static void setLimit(LocalTime timeLimit) {
        TimeUtil.timeLimit = timeLimit;
    }

    public static String toString(LocalTime localTime) {
        if (localTime == null) {
            return "";
        } else {
            return localTime.format(TIME_FORMATTER);
        }
    }

}
