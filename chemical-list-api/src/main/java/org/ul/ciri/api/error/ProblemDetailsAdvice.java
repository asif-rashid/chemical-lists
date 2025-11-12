package org.ul.ciri.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ul.ciri.listcatalog.exception.DuplicateLabelException;
import org.ul.ciri.listcatalog.exception.ListNotFoundException;
import org.ul.ciri.ratelimit.app.RateLimitExceededException;

@RestControllerAdvice
public class ProblemDetailsAdvice {

    private final ProblemDetailBuilder builder;

    public ProblemDetailsAdvice(ProblemDetailBuilder builder) {
        this.builder = builder;
    }

    @ExceptionHandler(ListNotFoundException.class)
    public ProblemDetail handleListNotFound(ListNotFoundException exception) {
        return builder.build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateLabelException.class)
    public ProblemDetail handleDuplicateLabel(DuplicateLabelException exception) {
        return builder.build(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimitExceeded(RateLimitExceededException exception) {
        return builder.build(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage());
    }
}
