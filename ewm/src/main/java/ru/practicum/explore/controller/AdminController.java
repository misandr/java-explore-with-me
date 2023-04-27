package ru.practicum.explore.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.Constants;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.Range;
import ru.practicum.explore.service.CategoryService;
import ru.practicum.explore.service.CompilationService;
import ru.practicum.explore.service.EventService;
import ru.practicum.explore.service.UserService;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;
    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserDto addUser(@RequestBody NewUserRequest newUserRequest) {
        log.info("Add new user {}", newUserRequest);
        return userService.addUser(newUserRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get users {} (from: {}, size: {})", ids, from, size);
        return userService.getUsers(ids, Range.of(from, size));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete user {}", userId);
        userService.deleteUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody NewCategoryDto newCategoryDto) {
        log.info("Add new category {}", newCategoryDto);
        return categoryService.addCategory(newCategoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Delete category {}", catId);
        categoryService.deleteCategory(catId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/categories/{catId}")
    public CategoryDto changeCategory(@PathVariable Long catId,
                                      @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Change category {} to {}", catId, newCategoryDto);
        return categoryService.changeCategory(catId, newCategoryDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/events/{eventId}")
    public EventFullDto changeEvent(@PathVariable Long eventId,
                                    @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("Change event {} to {}", eventId, updateEvent);
        return eventService.changeEvent(eventId, updateEvent);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(defaultValue = "") List<Long> users,
                                        @RequestParam(defaultValue = "") List<State> states,
                                        @RequestParam(defaultValue = "") List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get events of users {} with states {} and categories {} (rangeStart: {}, rangeEnd: {}, from: {}, size: {})",
                users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, Range.of(from, size));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("Add new compilation {}", newCompilationDto);
        return compilationService.addCompilation(newCompilationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Delete compilation {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/compilations/{compId}")
    public CompilationDto changeCompilation(@PathVariable Long compId,
                                            @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Change compId {} to {}", compId, updateCompilationRequest);
        return compilationService.changeCompilation(compId, updateCompilationRequest);
    }
}

