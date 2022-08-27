package kz.pichugin.restaurantvotingsystem.error;

import org.springframework.http.HttpStatus;

public class RestaurantNotFoundException extends RestaurantException {
    public RestaurantNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
    }
}
