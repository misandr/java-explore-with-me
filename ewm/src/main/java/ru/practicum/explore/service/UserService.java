package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.NewUserRequest;
import ru.practicum.explore.dto.UserDto;
import ru.practicum.explore.exceptions.ConflictException;
import ru.practicum.explore.exceptions.UserNotFoundException;
import ru.practicum.explore.exceptions.ValidationException;
import ru.practicum.explore.mapper.UserMapper;
import ru.practicum.explore.model.Range;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(NewUserRequest newUserRequest) {
        if (newUserRequest.getName() == null) {
            log.warn("User didn't save!");
            throw new ValidationException("User didn't save!");
        }

        try {
            User user = userRepository.save(UserMapper.toUser(newUserRequest));

            return UserMapper.toUserDto(user);
        } catch (RuntimeException e) {
            throw new ConflictException("User didn't save!");
        }
    }

    public UserDto getUserDto(Long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }

    public User getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            log.warn("Not found user " + userId);
            throw new UserNotFoundException(userId);
        }
    }

    public List<UserDto> getUsers(List<Long> ids, Range range) {
        int newFrom = range.getFrom() / range.getSize();
        Pageable page = PageRequest.of(newFrom, range.getSize());

        Page<User> usersPage = userRepository.findByIdIn(ids, page);

        return UserMapper.toListUserDto(usersPage.getContent());
    }

    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            log.warn("Not found user " + userId);
            throw new UserNotFoundException(userId);
        }
    }
}
