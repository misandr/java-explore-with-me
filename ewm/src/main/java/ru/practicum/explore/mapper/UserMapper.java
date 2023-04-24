package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.NewUserRequest;
import ru.practicum.explore.dto.UserDto;
import ru.practicum.explore.dto.UserShortDto;
import ru.practicum.explore.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static User toUser(NewUserRequest newUserRequest) {

        User user = new User();

        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());

        return user;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static List<UserDto> toListUserDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();

        for (User user : users) {
            usersDto.add(toUserDto(user));
        }

        return usersDto;
    }
}
