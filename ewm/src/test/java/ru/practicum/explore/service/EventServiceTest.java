package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.DateUtils;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.exceptions.NotFoundException;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Location;
import ru.practicum.explore.stats.StatsClient;
import ru.practicum.explore.stats.VisitDto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.explore.Constants.APP_NAME;
import static ru.practicum.explore.Constants.DATE_FORMAT;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceTest {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;

    @MockBean
    private final StatsClient statsClient;

    @Test
    void addEvent() {
        UserDto user = userService.addUser(new NewUserRequest("Иван", "j@j1.ru"));

        CategoryDto categoryFilms = categoryService.addCategory(new NewCategoryDto("Фильмы"));
        NewEventDto newEventDto = makeNewEvent("Кино 1", "Описание           Кино 1", "Аннотация           Кино 1", categoryFilms);

        EventFullDto addedEvent = eventService.addEvent(user.getId(), newEventDto);

        assertThat(addedEvent.getId(), notNullValue());
        assertThat(addedEvent.getCategory().getId(), equalTo(newEventDto.getCategory()));
        assertThat(addedEvent.getAnnotation(), equalTo(newEventDto.getAnnotation()));
        assertThat(addedEvent.getDescription(), equalTo(newEventDto.getDescription()));
        assertThat(addedEvent.getEventDate(), equalTo(newEventDto.getEventDate()));

        assertThat(addedEvent.getLocation(), equalTo(newEventDto.getLocation()));

        assertThat(addedEvent.getPaid(), equalTo(newEventDto.getPaid()));
        assertThat(addedEvent.getRequestModeration(), equalTo(newEventDto.getRequestModeration()));
        assertThat(addedEvent.getParticipantLimit(), equalTo(newEventDto.getParticipantLimit()));
        assertThat(addedEvent.getTitle(), equalTo(newEventDto.getTitle()));

        when(statsClient.getStats(any(), any(), any(), any()))
                .thenReturn(List.of(new VisitDto(APP_NAME, "/event/" + addedEvent.getId(), 1)));

        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();
        updateEventAdminRequest.setStateAction("PUBLISH_EVENT");
        eventService.changeEvent(addedEvent.getId(), updateEventAdminRequest);

        Event gotEvent = eventService.getEvent(addedEvent.getId(), "127.0.0.1", "/event/" + addedEvent.getId());
        assertThat(gotEvent.getId(), notNullValue());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        List<VisitDto> visits = statsClient.getStats(DateUtils.now().minusYears(5),
                DateUtils.now().plusYears(5), List.of(), false);

        assertThat(visits, hasSize(1));
    }

    @Test
    void changeEventBadId() {
        UserDto user = userService.addUser(new NewUserRequest("Иван", "j@j1.ru"));

        CategoryDto categoryFilms = categoryService.addCategory(new NewCategoryDto("Фильмы"));
        NewEventDto newEventDto = makeNewEvent("Кино 1",
                "Описание           Кино 1", "Аннотация           Кино 1", categoryFilms);

        EventFullDto addedEvent = eventService.addEvent(user.getId(), newEventDto);

        UpdateEventAdminRequest updateEvent = new UpdateEventAdminRequest();

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> eventService.changeEvent(1000L, updateEvent));

        Assertions.assertEquals("Not found event " + 1000L, exception.getMessage());
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