package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

    @Test
    void addUser() {
        NewUserRequest user = new NewUserRequest("Miss Diana Hilll", "Triston.Kertzmann@hotmail.com");

        UserDto addedUser = userService.addUser(user);

        assertThat(addedUser.getId(), notNullValue());
        assertThat(addedUser.getName(), equalTo(user.getName()));
        assertThat(addedUser.getEmail(), equalTo(user.getEmail()));
    }
}