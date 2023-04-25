package ru.practicum.explore.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.Constants;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.model.Range;
import ru.practicum.explore.service.CategoryService;
import ru.practicum.explore.service.CompilationService;
import ru.practicum.explore.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
public class PublicController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get categories (from: {}, size: {})", from, size);
        return categoryService.getCategories(Range.of(from, size));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("Get category {}", catId);
        return categoryService.getCategoryDto(catId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get compilations (pinned: {} from: {}, size: {})", pinned, from, size);
        return compilationService.getCompilations(pinned, Range.of(from, size));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Get compilation {}", compId);
        return compilationService.getCompilation(compId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/events/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Get event {}", eventId);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventService.getEventDto(eventId, request.getRemoteAddr(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/events")
    public Set<EventShortDto> searchEvents(@RequestParam(defaultValue = "") String text,
                                           @RequestParam(defaultValue = "") List<Long> categories,
                                           @RequestParam(defaultValue = "false") Boolean paid,
                                           @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                           @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           HttpServletRequest request) {
        log.info("Search events with text {}, categories {} , paid {} , onlyAvailable {} , sort {} (rangeStart: {}, rangeEnd: {}, from: {}, size: {})",
                text, categories, paid, onlyAvailable, sort, rangeStart, rangeEnd, from, size);
        return eventService.searchEvents(text, categories, paid, onlyAvailable, sort, rangeStart, rangeEnd,
                Range.of(from, size), request.getRemoteAddr(), request.getRequestURI());
    }
}

