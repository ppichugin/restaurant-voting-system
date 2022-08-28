package kz.pichugin.restaurantvotingsystem.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class RestaurantNotFoundException extends AppException {
    public RestaurantNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg, ErrorAttributeOptions.of(MESSAGE));
    }
}
