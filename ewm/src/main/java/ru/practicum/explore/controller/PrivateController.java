package ru.practicum.explore.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.model.EventRequestStatusUpdateRequest;
import ru.practicum.explore.model.EventRequestStatusUpdateResult;
import ru.practicum.explore.model.Range;
import ru.practicum.explore.service.EventService;
import ru.practicum.explore.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class PrivateController {
    private final RequestService requestService;
    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody NewEventDto newEventDto) {
        log.info("Add new event {}", newEventDto);
        return eventService.addEvent(userId, newEventDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventForCurrentUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get events for current user {}", userId);
        return eventService.getEventDtoForCurrentUser(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsForCurrentUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get events for current user {}", userId);
        return eventService.getEventsForCurrentUser(userId, Range.of(from, size));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto changeEventForCurrentUser(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("Change event {} by user {} to {}", eventId, userId, updateEvent);
        return eventService.changeEventByUser(userId, eventId, updateEvent);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get requests for current user {}", userId);
        return requestService.getRequests(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeStatusRequest(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Change status request for event {} of user {} to {}", eventId, userId, eventRequestStatusUpdateRequest);
        return requestService.changeStatusRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsOtherEvents(@PathVariable Long userId) {
        log.info("Get other events for current user {}", userId);
        return requestService.getOwnRequestsForOtherEvents(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Add request for event {} from user {}", eventId, userId);
        return requestService.addRequest(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Cancel request {} from user {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto changeComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody NewCommentDto newCommentDto) {
        log.info("Change comment {} to {} from user {}", commentId, newCommentDto, userId);
        return eventService.changeComment(userId, commentId, newCommentDto);
    }
}

