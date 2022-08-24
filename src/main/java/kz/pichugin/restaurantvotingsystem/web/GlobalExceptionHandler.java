package kz.pichugin.restaurantvotingsystem.web;

import kz.pichugin.restaurantvotingsystem.error.AppException;
import kz.pichugin.restaurantvotingsystem.error.DishException;
import kz.pichugin.restaurantvotingsystem.error.RestaurantException;
import kz.pichugin.restaurantvotingsystem.error.VoteException;
import kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Map<String, String> CONSTRAINS = new HashMap<>();

    public static final String EXCEPTION_DUPLICATE_EMAIL = "User with this email already exists";
    public static final String EXCEPTION_DUPLICATE_DISH = "The dish in this restaurant already exists";
    public static final String EXCEPTION_PAST_DAYS_DISH = "Can not delete food for the past days";
    public static final String EXCEPTION_DISH_NOT_FOUND = "Dish not found";
    public static final String EXCEPTION_DUPLICATE_RESTAURANT = "The restaurant already exists";
    public static final String EXCEPTION_RESTAURANT_WITH_HISTORY = "Unable to delete.The restaurant has menu history";
    public static final String EXCEPTION_RESTAURANT_NOT_FOUND = "RestaurantId not found";
    public static final String EXCEPTION_TIME_LIMIT_VOTE = "Time limit to change the vote is: ";
    public static final String EXCEPTION_VOTE_NOT_FOUND = "Vote not found";

    static {
        CONSTRAINS.put("uk_dish", EXCEPTION_DUPLICATE_DISH);
        CONSTRAINS.put("uk_restaurant", EXCEPTION_DUPLICATE_RESTAURANT);
    }

    private final ErrorAttributes errorAttributes;

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(WebRequest request, AppException ex) {
        log.error("ApplicationException: {}", ex.getMessage());
        return createResponseEntity(getDefaultBody(request, ex.getOptions(), null), ex.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> persistException(WebRequest request, EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage());
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(RestaurantException.class)
    public ResponseEntity<?> persistException(WebRequest request, RestaurantException ex) {
        log.error("RestaurantException: {}", ex.getMessage());
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DishException.class)
    public ResponseEntity<?> persistException(WebRequest request, DishException ex) {
        log.error("DishException: {}", ex.getMessage());
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(VoteException.class)
    public ResponseEntity<?> persistException(WebRequest request, VoteException ex) {
        log.error("VoteException: {}", ex.getMessage());
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request, DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage());
        String rootMsg = ValidationUtil.getRootCause(ex).getMessage();
        if (rootMsg != null) {
            String errorMessage = rootMsg.toLowerCase();
            for (Map.Entry<String, String> entry : CONSTRAINS.entrySet()) {
                if (errorMessage.contains(entry.getKey())) {
                    return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), entry.getValue()), HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
        }
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> handleBindingErrors(BindingResult result, WebRequest request) {
        String msg = result.getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.joining("\n"));
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), msg), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private Map<String, Object> getDefaultBody(WebRequest request, ErrorAttributeOptions options, String msg) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, options);
        if (msg != null) {
            body.put("message", msg);
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> createResponseEntity(Map<String, Object> body, HttpStatus status) {
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("Exception", ex);
        super.handleExceptionInternal(ex, body, headers, status, request);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(), ValidationUtil.getRootCause(ex).getMessage()), status);
    }
}