package ru.practicum.explore.exceptions;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(final long userId) {
        super("Not found user " + userId);
    }
}
