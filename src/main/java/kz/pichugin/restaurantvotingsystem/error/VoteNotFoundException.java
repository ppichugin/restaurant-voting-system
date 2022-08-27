package kz.pichugin.restaurantvotingsystem.error;

import org.springframework.http.HttpStatus;

public class VoteNotFoundException extends VoteException{
    public VoteNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
    }
}
