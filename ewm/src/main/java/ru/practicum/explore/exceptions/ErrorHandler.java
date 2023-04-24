package ru.practicum.explore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice("ru.practicum.explore")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectValidationException(final ValidationException e) {
        return new ApiError(e.getMessage(), "The required object was not found.", HttpStatus.BAD_REQUEST, List.of());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        return new ApiError(e.getMessage(), "The required object was forbidden.", HttpStatus.FORBIDDEN, List.of());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(e.getMessage(), "The required object was not found.", HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIncorrectValidationException(final ConflictException e) {
        return new ApiError(e.getMessage(), "The required object has conflict.", HttpStatus.CONFLICT, List.of());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleThrowable(final Throwable e) {
        return new ApiError("An unexpected error has occurred.", "The required object was not found.", HttpStatus.BAD_REQUEST, List.of());

    }
}
