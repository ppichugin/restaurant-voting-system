package kz.pichugin.restaurantvotingsystem.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class RestaurantException extends AppException {
    public RestaurantException(String msg) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, msg, ErrorAttributeOptions.of(MESSAGE));
    }

    public RestaurantException(HttpStatus httpStatus, String msg) {
        super(httpStatus, msg, ErrorAttributeOptions.of(MESSAGE));
    }
}
