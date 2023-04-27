package ru.practicum.explore.exceptions;

import org.springframework.http.HttpStatus;
import ru.practicum.explore.DateUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.explore.Constants.DATE_FORMAT;

public class ApiError {

    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;

    public ApiError(String message, String reason, HttpStatus statusCode, List<String> errors) {
        this.message = message;
        this.reason = reason;
        this.status = statusCode.name();
        this.errors = errors;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        timestamp = DateUtils.now().format(formatter);
    }

    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getTimestamp() {
        return timestamp;
    }
}