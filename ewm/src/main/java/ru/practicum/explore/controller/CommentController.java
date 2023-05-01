package ru.practicum.explore.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.Constants;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.model.Range;
import ru.practicum.explore.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explore.Constants.HEADER_USER_ID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/comments")
public class CommentController {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto addComment(@RequestHeader(HEADER_USER_ID) Long userId,
                                 @RequestParam(defaultValue = "0") Long eventId,
                                 @RequestBody NewCommentDto newCommentDto) {
        log.info("Add comment {} for event {} from user {}", newCommentDto, eventId, userId);
        return eventService.addComment(userId, eventId, newCommentDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{commentId}")
    public CommentDto changeComment(@RequestHeader(HEADER_USER_ID) Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody NewCommentDto newCommentDto) {
        log.info("Change comment {} to {} from user {}", commentId, newCommentDto, userId);
        return eventService.changeComment(userId, commentId, newCommentDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CommentDto> getComments(@RequestParam(defaultValue = "") List<Long> users,
                                        @RequestParam(defaultValue = "") List<Long> events,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get comments of users {} and events {} (from: {}, size: {})",
                users, events, from, size);

        return eventService.getComments(users, events, Range.of(from, size));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        log.info("Get comment by id {}", commentId);

        return eventService.getCommentDto(commentId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long commentId) {
        log.info("Delete comment with id {} by user {}", commentId, userId);

        eventService.deleteComment(userId, commentId);
    }
}

