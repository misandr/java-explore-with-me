package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.DateUtils;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.model.Location;
import ru.practicum.explore.model.Range;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CompilationServiceTest {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;
    private final CompilationService compilationService;

    @Test
    void changeCompilation() {
        UserDto user1 = userService.addUser(new NewUserRequest("Иван 1", "j@j1.ru"));

        CategoryDto categoryFilms = categoryService.addCategory(new NewCategoryDto("Фильмы"));
        CategoryDto categoryConcerts = categoryService.addCategory(new NewCategoryDto("Концерты"));

        NewEventDto film1 = makeNewEvent("Кино 1", "Описание           Кино 1", "Аннотация           Кино 1", categoryFilms);
        NewEventDto film2 = makeNewEvent("Кино 2", "Описание           Кино 2", "Аннотация           Кино 2", categoryFilms);

        EventFullDto eventFilm1 = eventService.addEvent(user1.getId(), film1);
        EventFullDto eventFilm2 = eventService.addEvent(user1.getId(), film2);


        CompilationDto compilationFilms = compilationService.addCompilation(
                new NewCompilationDto(List.of(eventFilm1.getId(), eventFilm2.getId()), false, "Фильмы"));

        List<CompilationDto> gotNotPinnedCompilations = compilationService.getCompilations(false, Range.of(0, 10));

        List<CompilationDto> sourceNotPinnedCompilationsDto = List.of(compilationFilms);
        assertThat(gotNotPinnedCompilations, hasSize(sourceNotPinnedCompilationsDto.size()));
        for (CompilationDto compilation : sourceNotPinnedCompilationsDto) {
            assertThat(gotNotPinnedCompilations, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("pinned", equalTo(compilation.getPinned())),
                    hasProperty("title", equalTo(compilation.getTitle()))
            )));
            assertThat(compilation.getEvents(), hasSize(2));
        }

        compilationService.changeCompilation(compilationFilms.getId(),
                new UpdateCompilationRequest(List.of(eventFilm1.getId()), true, "Фильмы"));

        CompilationDto compilationChangedFilms = compilationService.getCompilation(compilationFilms.getId());
        List<CompilationDto> gotPinnedCompilations = compilationService.getCompilations(true, Range.of(0, 10));

        List<CompilationDto> sourcePinnedCompilationsDto = List.of(compilationChangedFilms);
        assertThat(gotPinnedCompilations, hasSize(sourcePinnedCompilationsDto.size()));
        for (CompilationDto compilation : sourcePinnedCompilationsDto) {
            assertThat(gotPinnedCompilations, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("pinned", equalTo(compilation.getPinned())),
                    hasProperty("title", equalTo(compilation.getTitle()))
            )));
            assertThat(compilation.getEvents(), hasSize(1));
        }
    }

    @Test
    void getCompilations() {
        UserDto user1 = userService.addUser(new NewUserRequest("Иван 1", "j@j1.ru"));
        UserDto user2 = userService.addUser(new NewUserRequest("Иван 2", "j@j2.ru"));
        UserDto user3 = userService.addUser(new NewUserRequest("Иван 3", "j@j3.ru"));

        CategoryDto categoryFilms = categoryService.addCategory(new NewCategoryDto("Фильмы"));
        CategoryDto categoryConcerts = categoryService.addCategory(new NewCategoryDto("Концерты"));

        NewEventDto film1 = makeNewEvent("Кино 1", "Описание           Кино 1", "Аннотация           Кино 1", categoryFilms);
        NewEventDto film2 = makeNewEvent("Кино 2", "Описание           Кино 2", "Аннотация           Кино 2", categoryFilms);

        NewEventDto concert1 = makeNewEvent("Концерт 1", "Описание           Концерт 1", "Аннотация           Концерт 1", categoryConcerts);

        EventFullDto eventFilm1 = eventService.addEvent(user1.getId(), film1);
        EventFullDto eventFilm2 = eventService.addEvent(user2.getId(), film2);

        EventFullDto eventConcert1 = eventService.addEvent(user3.getId(), concert1);

        CompilationDto compilationFilms = compilationService.addCompilation(
                new NewCompilationDto(List.of(eventFilm1.getId(), eventFilm2.getId()), false, "Фильмы"));
        CompilationDto compilationConcerts = compilationService.addCompilation(
                new NewCompilationDto(List.of(eventConcert1.getId()), true, "Концерты"));


        List<CompilationDto> gotNotPinnedCompilations = compilationService.getCompilations(false, Range.of(0, 10));
        List<CompilationDto> gotPinnedCompilations = compilationService.getCompilations(true, Range.of(0, 10));

        List<CompilationDto> sourceNotPinnedCompilationsDto = List.of(compilationFilms);
        assertThat(gotNotPinnedCompilations, hasSize(sourceNotPinnedCompilationsDto.size()));
        for (CompilationDto compilation : sourceNotPinnedCompilationsDto) {
            assertThat(gotNotPinnedCompilations, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("pinned", equalTo(compilation.getPinned())),
                    hasProperty("title", equalTo(compilation.getTitle()))
            )));
            assertThat(compilation.getEvents(), hasSize(2));
        }

        List<CompilationDto> sourcePinnedCompilationsDto = List.of(compilationConcerts);
        assertThat(gotNotPinnedCompilations, hasSize(sourcePinnedCompilationsDto.size()));
        for (CompilationDto compilation : sourcePinnedCompilationsDto) {
            assertThat(gotPinnedCompilations, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("pinned", equalTo(compilation.getPinned())),
                    hasProperty("title", equalTo(compilation.getTitle()))
            )));
            assertThat(compilation.getEvents(), hasSize(1));
        }

        System.out.println(gotNotPinnedCompilations);
        System.out.println(gotPinnedCompilations);
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