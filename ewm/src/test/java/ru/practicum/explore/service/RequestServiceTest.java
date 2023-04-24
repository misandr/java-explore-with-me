package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.DateUtils;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.enums.RequestStatus;
import ru.practicum.explore.model.EventRequestStatusUpdateRequest;
import ru.practicum.explore.model.Location;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceTest {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;
    private final RequestService requestService;

    @Test
    void changeStatusRequest() {
        UserDto user1 = userService.addUser(new NewUserRequest("Иван 1", "j@j1.ru"));
        UserDto user2 = userService.addUser(new NewUserRequest("Иван 2", "j@j2.ru"));
        UserDto user3 = userService.addUser(new NewUserRequest("Иван 3", "j@j3.ru"));

        CategoryDto categoryFilms = categoryService.addCategory(new NewCategoryDto("Фильмы"));

        NewEventDto film1 = makeNewEvent("Кино 1", "Описание           Кино 1", "Аннотация           Кино 1", categoryFilms);

        EventFullDto eventFilm1 = eventService.addEvent(user1.getId(), film1);

        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();
        updateEventAdminRequest.setStateAction("PUBLISH_EVENT");
        updateEventAdminRequest.setParticipantLimit(10);
        updateEventAdminRequest.setRequestModeration(true);

        eventService.changeEvent(eventFilm1.getId(), updateEventAdminRequest);

        requestService.addRequest(user2.getId(), eventFilm1.getId());

        List<ParticipationRequestDto> requests = requestService.getRequests(user1.getId(), eventFilm1.getId());

        assertThat(requests, hasSize(1));

        assertThat(requests.get(0).getStatus(), equalTo(RequestStatus.PENDING));

        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest =
                new EventRequestStatusUpdateRequest(List.of(1L), RequestStatus.CONFIRMED);

        requestService.changeStatusRequest(user1.getId(), eventFilm1.getId(), eventRequestStatusUpdateRequest);

        requests = requestService.getRequests(user1.getId(), eventFilm1.getId());

        assertThat(requests.get(0).getStatus(), equalTo(RequestStatus.CONFIRMED));
    }

    NewEventDto makeNewEvent(String title, String description, String annotation, CategoryDto category) {

        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setCategory(category.getId());
        newEventDto.setAnnotation(annotation);
        newEventDto.setDescription(description);
        newEventDto.setEventDate(DateUtils.now().plusHours(5));
        newEventDto.setLocation(Location.of((float) 55.754167, (float) 37.62));
        newEventDto.setPaid(true);
        newEventDto.setRequestModeration(true);
        newEventDto.setParticipantLimit(10);
        newEventDto.setTitle(title);
        return newEventDto;
    }
}