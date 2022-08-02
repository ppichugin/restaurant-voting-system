package kz.pichugin.restaurantvotingsystem.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class RestaurantNotFoundException extends AppException {
    public RestaurantNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, ErrorAttributeOptions.of(MESSAGE));
    }

    public RestaurantNotFoundException(int id) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, String.valueOf(id), ErrorAttributeOptions.of(MESSAGE));
    }
}
