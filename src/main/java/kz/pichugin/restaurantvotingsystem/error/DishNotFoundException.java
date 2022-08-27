package kz.pichugin.restaurantvotingsystem.error;

import org.springframework.http.HttpStatus;

public class DishNotFoundException extends DishException {
    public DishNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
    }
}
