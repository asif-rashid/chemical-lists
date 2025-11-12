package com.example.chemicallists.problem;

import com.example.chemicallists.catalog.ListLabelAlreadyExistsException;
import com.example.chemicallists.catalog.ListNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_TYPE_PREFIX = "https://api.example.org/errors/";

    @ExceptionHandler(ListNotFoundException.class)
    public ResponseEntity<ProblemDetailsResponse> handleNotFound(ListNotFoundException ex, HttpServletRequest request) {
        return buildProblem("not-found", "Not Found", HttpStatus.NOT_FOUND,
                "List %d was not found.".formatted(ex.getListId()), request.getRequestURI());
    }

    @ExceptionHandler(ListLabelAlreadyExistsException.class)
    public ResponseEntity<ProblemDetailsResponse> handleConflict(ListLabelAlreadyExistsException ex, HttpServletRequest request) {
        return buildProblem("conflict", "Conflict", HttpStatus.CONFLICT,
                "List label '%s' already exists.".formatted(ex.getLabel()), request.getRequestURI());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ProblemDetailsResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        String detail;
        if (ex instanceof MethodArgumentNotValidException manv) {
            detail = manv.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof BindException bind) {
            detail = bind.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof ConstraintViolationException violation) {
            detail = violation.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining(", "));
        } else {
            detail = ex.getMessage();
        }
        return buildProblem("bad-request", "Bad Request", HttpStatus.BAD_REQUEST, detail, request.getRequestURI());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetailsResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return buildProblem("unauthorized", "Missing or invalid credentials", HttpStatus.UNAUTHORIZED,
                ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetailsResponse> handleForbidden(AccessDeniedException ex, HttpServletRequest request) {
        return buildProblem("forbidden", "Forbidden", HttpStatus.FORBIDDEN,
                ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailsResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        return buildProblem("internal-server-error", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ProblemDetailsResponse> buildProblem(String typeSuffix,
                                                                 String title,
                                                                 HttpStatus status,
                                                                 String detail,
                                                                 String instance) {
        String type = DEFAULT_TYPE_PREFIX + typeSuffix;
        ProblemDetailsResponse body = ProblemDetailsResponse.of(type, title, status.value(), detail, instance);
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(body);
    }
}
