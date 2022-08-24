package kz.pichugin.restaurantvotingsystem.util.validation;

import kz.pichugin.restaurantvotingsystem.HasId;
import kz.pichugin.restaurantvotingsystem.error.IllegalRequestDataException;
import kz.pichugin.restaurantvotingsystem.error.VoteException;
import lombok.experimental.UtilityClass;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;

import java.time.LocalTime;

import static kz.pichugin.restaurantvotingsystem.util.TimeUtil.TIME_FORMATTER;
import static kz.pichugin.restaurantvotingsystem.util.TimeUtil.getLimit;
import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_TIME_LIMIT_VOTE;

@UtilityClass
public class ValidationUtil {

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    //  Conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must has id=" + id);
        }
    }

    public static void checkModification(int count, int id) {
        if (count == 0) {
            throw new IllegalRequestDataException("Entity with id=" + id + " not found");
        }
    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    public static void assureTimeLimit(LocalTime currentTime) {
        if (currentTime.isAfter(getLimit())) {
            throw new VoteException(EXCEPTION_TIME_LIMIT_VOTE + getLimit().format(TIME_FORMATTER) + ", but it was a try to vote at: " + currentTime.format(TIME_FORMATTER));
        }
    }
}