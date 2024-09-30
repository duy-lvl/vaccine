package fa.training.backend.exception.handler;


import fa.training.backend.dto.response.Response;
import fa.training.backend.exception.CustomException;
import fa.training.backend.exception.common.InvalidListException;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.vaccine.InvalidVaccineImportException;
import fa.training.backend.util.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Response> usernameNotFound(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        Response.builder()
                                .code(Message.MSG_101.getCode())
                                .description(Message.MSG_101.getMessage())
                                .data(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.builder()
                        .code(ex.getDescription().getCode())
                        .description(ex.getDescription().getMessage())
                        .data(ex.getId())
                        .build());
    }
    @ExceptionHandler(InvalidListException.class)
    public ResponseEntity<Response> handleInvalidListException(InvalidListException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.builder()
                        .code(ex.getDescription().getCode())
                        .description(ex.getDescription().getMessage())
                        .data(ex.getIds())
                        .build());
    }
    @ExceptionHandler(InvalidVaccineImportException.class)
    public ResponseEntity<Response> handleInvalidVaccineImportException(InvalidVaccineImportException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.builder()
                        .code(ex.getDescription().getCode())
                        .description(ex.getDescription().getMessage())
                        .data(ex.getErrorData())
                        .build());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response> handleCustomException(CustomException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.builder()
                        .code(ex.getDescription().getCode())
                        .description(ex.getDescription().getMessage())
                        .build());
    }

    // General exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.builder()
                        .code(Message.MSG_400.getCode())
                        .description(Message.MSG_400.getMessage())
                        .data(ex)
                        .build());
    }
}