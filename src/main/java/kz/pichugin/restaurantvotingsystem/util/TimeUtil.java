package kz.pichugin.restaurantvotingsystem.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeUtil {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static LocalTime timeLimit = LocalTime.of(11, 0);

    public static LocalTime getLimit() {
        return timeLimit;
    }

    public static void setLimit(LocalTime timeLimit) {
        TimeUtil.timeLimit = timeLimit;
    }

    @NotNull
    public static String toString(@NotNull LocalTime localTime) {
        return localTime.format(TIME_FORMATTER);
    }
}
