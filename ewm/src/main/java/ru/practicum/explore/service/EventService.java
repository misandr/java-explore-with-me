package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.DateUtils;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.exceptions.ConflictException;
import ru.practicum.explore.exceptions.NotFoundException;
import ru.practicum.explore.exceptions.ValidationException;
import ru.practicum.explore.mapper.CommentMapper;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.model.*;
import ru.practicum.explore.repository.CommentRepository;
import ru.practicum.explore.repository.EventRepository;
import ru.practicum.explore.repository.RequestRepository;
import ru.practicum.explore.stats.HitDto;
import ru.practicum.explore.stats.StatsClient;
import ru.practicum.explore.stats.VisitDto;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.explore.Constants.APP_NAME;
import static ru.practicum.explore.enums.RequestStatus.CONFIRMED;
import static ru.practicum.explore.enums.State.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final UserService userService;
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getTitle() == null) {
            log.warn("Event didn't save!");
            throw new ValidationException("Event didn't save!");
        }

        if (newEventDto.getDescription() == null) {
            log.warn("Event didn't save!");
            throw new ValidationException("Event didn't save!");
        }

        if (newEventDto.getAnnotation() == null) {
            log.warn("Event didn't save!");
            throw new ValidationException("Event didn't save!");
        }

        if (newEventDto.getEventDate().isBefore(DateUtils.now())) {
            log.warn("Event didn't save!");
            throw new ConflictException("Event didn't save!");
        }

        Event event = new Event();

        event.setInitiator(userService.getUser(userId));
        event.setCategory(categoryService.getCategory(newEventDto.getCategory()));
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());
        event.setPaid(newEventDto.getPaid());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setTitle(newEventDto.getTitle());
        event.setRequestModeration(newEventDto.getRequestModeration());

        event.setState(PENDING);

        event.setCreatedOn(DateUtils.now());

        try {
            Event savedEvent = eventRepository.save(event);

            return EventMapper.toEventFullDto(savedEvent);
        } catch (RuntimeException e) {
            throw new ConflictException("Event didn't save!");
        }
    }

    public Event getEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            return event.get();
        } else {
            log.warn("Not found event " + eventId);
            throw new NotFoundException("Not found event " + eventId);
        }
    }

    public Event getEvent(Long eventId, String ip, String endpoint) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            Event gotEvent = event.get();
            statsClient.addHit(new HitDto(APP_NAME, endpoint, ip, DateUtils.now()));

            if (event.get().getState() != PUBLISHED) {
                log.warn("Not found event " + eventId);
                throw new NotFoundException("Not found event " + eventId);
            }

            return gotEvent;
        } else {
            log.warn("Not found event " + eventId);
            throw new NotFoundException("Not found event " + eventId);
        }
    }

    public EventFullDto getEventDto(Long eventId, String ip, String endpoint) {
        Event gotEvent = getEvent(eventId, ip, endpoint);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(gotEvent);

        List<VisitDto> visits = statsClient.getStats(
                DateUtils.now(), DateUtils.now().plusYears(100), List.of("/events/" + eventFullDto.getId()), true);

        long hits = 0;

        for (VisitDto visit : visits) {
            if (visit.getUri().equals("/event/" + eventFullDto.getId())) {
                hits = visit.getHits();
                break;
            }
        }

        List<ParticipationRequest> requests = requestRepository.findByEvent(gotEvent);

        long countConfirmed = 0;
        for (ParticipationRequest request : requests) {
            if (request.getStatus() == CONFIRMED) {
                countConfirmed++;
            }
        }

        eventFullDto.setViews(hits);
        eventFullDto.setConfirmedRequests(countConfirmed);

        List<Comment> comments = commentRepository.findByEvent(gotEvent);
        eventFullDto.setComments(CommentMapper.toCommentsDto(comments));

        return eventFullDto;
    }

    public Event getEventForCurrentUser(Long userId, Long eventId) {
        User user = userService.getUser(userId);
        return eventRepository.findByIdAndInitiator(eventId, user);
    }

    public EventFullDto getEventDtoForCurrentUser(Long userId, Long eventId) {
        EventFullDto eventFullDto = EventMapper.toEventFullDto(getEventForCurrentUser(userId, eventId));
        Event gotEvent = getEvent(eventId);

        List<Comment> comments = commentRepository.findByEvent(gotEvent);
        eventFullDto.setComments(CommentMapper.toCommentsDto(comments));
        return eventFullDto;
    }

    public List<EventShortDto> getEventsForCurrentUser(Long userId, Range range) {
        User user = userService.getUser(userId);

        int newFrom = range.getFrom() / range.getSize();
        Pageable page = PageRequest.of(newFrom, range.getSize());

        Page<Event> eventsPage = eventRepository.findByInitiator(user, page);

        List<EventShortDto> events = new ArrayList<>();

        for (Event event : eventsPage.getContent()) {
            EventShortDto eventShortDto = EventMapper.toEventShortDto(event);

            List<Comment> comments = commentRepository.findByEvent(event);

            eventShortDto.setComments(CommentMapper.toCommentsDto(comments));

            events.add(eventShortDto);
        }
        return events;
    }

    public EventFullDto changeEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {

        if (eventRepository.existsById(eventId)) {
            User user = userService.getUser(userId);
            Event gotEvent = eventRepository.getReferenceById(eventId);

            if (gotEvent.getInitiator().equals(user)) {

                if (gotEvent.getState() == PUBLISHED) {
                    log.warn("Event " + eventId + " didn't update!");
                    throw new ConflictException("Event " + eventId + " didn't update!");
                }

                if (((gotEvent.getState() == CANCELED) || (gotEvent.getState() == PENDING))
                        && (gotEvent.getEventDate().isAfter(DateUtils.now().plusHours(2)))) {

                    if (updateEvent.getTitle() != null) {
                        gotEvent.setTitle(updateEvent.getTitle());
                    }

                    if (updateEvent.getDescription() != null) {
                        gotEvent.setDescription(updateEvent.getDescription());
                    }

                    if (updateEvent.getAnnotation() != null) {
                        gotEvent.setAnnotation(updateEvent.getAnnotation());
                    }

                    if (updateEvent.getStateAction() != null) {
                        switch (updateEvent.getStateAction()) {
                            case "SEND_TO_REVIEW":
                                gotEvent.setState(PENDING);
                                break;
                            case "CANCEL_REVIEW":
                                gotEvent.setState(CANCELED);
                                break;
                            default:
                                break;
                        }
                    }

                    if (updateEvent.getEventDate() != null) {

                        if (updateEvent.getEventDate().isBefore(gotEvent.getEventDate())) {
                            log.warn("Event " + eventId + " didn't update!");
                            throw new ConflictException("Event " + eventId + " didn't update!");
                        }

                        gotEvent.setEventDate(updateEvent.getEventDate());
                    }

                    if (updateEvent.getLocation() != null) {
                        gotEvent.setLat(updateEvent.getLocation().getLat());
                        gotEvent.setLon(updateEvent.getLocation().getLon());
                    }

                    if (updateEvent.getPaid() != null) {
                        gotEvent.setPaid(updateEvent.getPaid());
                    }

                    if (updateEvent.getRequestModeration() != null) {
                        gotEvent.setRequestModeration(updateEvent.getRequestModeration());
                    }

                    try {
                        return EventMapper.toEventFullDto(eventRepository.save(gotEvent));
                    } catch (RuntimeException e) {
                        log.warn("Event didn't update " + eventId);
                        throw new ConflictException("Event didn't update " + eventId);
                    }
                } else {
                    log.warn("Event didn't update " + eventId);
                    throw new ConflictException("Event didn't update " + eventId);
                }
            } else {
                log.warn("Not found event " + eventId);
                throw new NotFoundException("Not found event " + eventId);
            }

        } else {
            log.warn("Not event category " + eventId);
            throw new NotFoundException("Not found event " + eventId);
        }
    }

    public EventFullDto changeEvent(Long eventId, UpdateEventAdminRequest updateEvent) {

        if (eventRepository.existsById(eventId)) {
            Event gotEvent = eventRepository.getReferenceById(eventId);

            if (gotEvent.getState() == PUBLISHED) {
                if (gotEvent.getEventDate().isBefore(gotEvent.getPublishedOn().plusHours(1))) {
                    log.warn("Event " + eventId + " didn't update (wrong date)!");
                    throw new ConflictException("Event " + eventId + " didn't update (wrong date)!");
                }

                if (updateEvent.getStateAction().equals("REJECT_EVENT")) {
                    log.warn("Event " + eventId + " didn't update!");
                    throw new ConflictException("Event " + eventId + " didn't update!");
                }
            }

            if (gotEvent.getState() != PENDING) {
                if (updateEvent.getStateAction().equals("PUBLISH_EVENT")) {
                    log.warn("Event " + eventId + " didn't update (wrong state)!");
                    throw new ConflictException("Event " + eventId + " didn't update (wrong state)!");
                }
            }

            if (updateEvent.getEventDate() != null) {

                if (updateEvent.getEventDate().isBefore(gotEvent.getEventDate())) {
                    log.warn("Event " + eventId + " didn't update!");
                    throw new ConflictException("Event " + eventId + " didn't update!");
                }

                gotEvent.setEventDate(updateEvent.getEventDate());
            }

            if (updateEvent.getTitle() != null) {
                gotEvent.setTitle(updateEvent.getTitle());
            }

            if (updateEvent.getDescription() != null) {
                gotEvent.setDescription(updateEvent.getDescription());
            }

            if (updateEvent.getAnnotation() != null) {
                gotEvent.setAnnotation(updateEvent.getAnnotation());
            }

            if (updateEvent.getStateAction() != null) {
                switch (updateEvent.getStateAction()) {
                    case "PUBLISH_EVENT":
                        gotEvent.setState(PUBLISHED);
                        gotEvent.setPublishedOn(DateUtils.now());
                        break;
                    case "REJECT_EVENT":
                        gotEvent.setState(CANCELED);
                        break;
                    default:
                        break;
                }
            }

            if (updateEvent.getCategory() != null) {
                Category category = categoryService.getCategory(updateEvent.getCategory());
                gotEvent.setCategory(category);
            }

            if (updateEvent.getEventDate() != null) {
                gotEvent.setEventDate(updateEvent.getEventDate());
            }

            if (updateEvent.getLocation() != null) {
                gotEvent.setLat(updateEvent.getLocation().getLat());
                gotEvent.setLon(updateEvent.getLocation().getLon());
            }

            if (updateEvent.getParticipantLimit() != null) {
                gotEvent.setParticipantLimit(updateEvent.getParticipantLimit());
            }

            if (updateEvent.getPaid() != null) {
                gotEvent.setPaid(updateEvent.getPaid());
            }

            if (updateEvent.getRequestModeration() != null) {
                gotEvent.setRequestModeration(updateEvent.getRequestModeration());
            }

            try {
                Event event = eventRepository.save(gotEvent);

                return EventMapper.toEventFullDto(event);
            } catch (RuntimeException e) {
                log.warn("Event " + eventId + " didn't update!");
                throw new ConflictException("Event " + eventId + " didn't update!");
            }

        } else {
            log.warn("Not found event " + eventId);
            throw new NotFoundException("Not found event " + eventId);
        }
    }

    public List<EventFullDto> getEvents(List<Long> users, List<State> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Range range) {

        int newFrom = range.getFrom() / range.getSize();
        Pageable page = PageRequest.of(newFrom, range.getSize());

        Page<Event> eventsPage =
                eventRepository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsAfterAndEventDateIsBefore(
                        users, states, categories,
                        rangeStart, rangeEnd, page);

        List<EventFullDto> events = new ArrayList<>();

        for (Event event : eventsPage.getContent()) {
            EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

            List<Comment> comments = commentRepository.findByEvent(event);

            eventFullDto.setComments(CommentMapper.toCommentsDto(comments));

            events.add(eventFullDto);
        }

        return events;
    }

    public Set<EventShortDto> searchEvents(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                           String sort, LocalDateTime rangeStart, LocalDateTime rangeEnd, Range range,
                                           String ip, String endpoint) {

        statsClient.addHit(new HitDto(APP_NAME, endpoint, ip, DateUtils.now()));

        List<EventShortDto> events = null;
        int newFrom = range.getFrom() / range.getSize();

        Pageable page = PageRequest.of(newFrom, range.getSize());

        Page<Event> eventsPage;

        if ((rangeStart == null) || (rangeEnd == null)) {
            if (onlyAvailable) {
                eventsPage =
                        eventRepository.findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndEventDateIsBeforeAndPaidIsAndStateIs(
                                text, text, State.PUBLISHED, categories,
                                rangeStart, rangeEnd, paid, true, page);
            } else {
                eventsPage =
                        eventRepository.findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndEventDateIsBeforeAndPaidIs(
                                text, text, State.PUBLISHED, categories,
                                rangeStart, rangeEnd, paid, page);
            }
        } else {
            if (onlyAvailable) {
                eventsPage =
                        eventRepository.findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndPaidIsAndStateIs(
                                text, text, State.PUBLISHED, categories,
                                DateUtils.now(), paid, true, page);
            } else {
                eventsPage =
                        eventRepository.findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndPaidIs(
                                text, text, State.PUBLISHED, categories,
                                DateUtils.now(), paid, page);
            }
        }
        events = EventMapper.toListEventShortDto(eventsPage.getContent());

        List<String> uris = new ArrayList<>();
        for (EventShortDto eventShortDto : events) {
            uris.add("/events/" + eventShortDto.getId());
        }

        Comparator<EventShortDto> comparator;
        if (sort.equals("VIEWS")) {
            comparator = new Comparator<EventShortDto>() {
                @Override
                public int compare(EventShortDto o1, EventShortDto o2) {
                    if (o1.getViews() < o2.getViews()) {
                        return 1;
                    } else if (o1.getViews() > o2.getViews()) {
                        return -1;
                    } else {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                }
            };
        } else if (sort.equals("EVENT_DATE")) {
            comparator = new Comparator<EventShortDto>() {
                @Override
                public int compare(EventShortDto o1, EventShortDto o2) {
                    if (o1.getEventDate().isAfter(o2.getEventDate())) {
                        return 1;
                    } else if (o2.getEventDate().isAfter(o1.getEventDate())) {
                        return -1;
                    } else {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                }
            };
        } else {
            log.warn("Bad type sort!");
            throw new ValidationException("Bad type sort!");
        }

        Set<EventShortDto> sortedEvents = new TreeSet<>(comparator);

        List<VisitDto> visits;
        if ((rangeStart == null) || (rangeEnd == null)) {
            visits = statsClient.getStats(DateUtils.now(), DateUtils.now().plusYears(100), uris, true);
        } else {
            visits = statsClient.getStats(rangeStart, rangeEnd, uris, true);
        }

        for (EventShortDto eventShortDto : events) {

            long hits = 0;

            for (VisitDto visit : visits) {
                if (visit.getUri().equals("/event/" + eventShortDto.getId())) {
                    hits = visit.getHits();
                    break;
                }
            }

            Event gotEvent = getEvent(eventShortDto.getId());

            List<ParticipationRequest> requests = requestRepository.findByEvent(gotEvent);

            long countConfirmed = 0;
            for (ParticipationRequest request : requests) {
                if (request.getStatus() == CONFIRMED) {
                    countConfirmed++;
                }
            }

            eventShortDto.setViews(hits);
            eventShortDto.setConfirmedRequests(countConfirmed);

            List<Comment> comments = commentRepository.findByEvent(gotEvent);
            eventShortDto.setComments(CommentMapper.toCommentsDto(comments));

            sortedEvents.add(eventShortDto);
        }

        return sortedEvents;
    }

    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userService.getUser(userId);
        Event event = getEvent(eventId);
        List<ParticipationRequest> requests = requestRepository.findByEventAndRequesterAndStatusIs(event, user, CONFIRMED);

        if (requests.size() > 0) {
            if (newCommentDto.getText().isBlank()) {
                log.warn("Can't add comment. Text of comment is empty!");
                throw new ConflictException("Can't add comment. Text of comment is empty!");
            }

            if (event.getEventDate().isAfter(DateUtils.now())) {
                log.warn("Can't add comment. Event not yet!");
                throw new ConflictException("Can't add comment. Event not yet!");
            }

            Comment comment = new Comment();

            comment.setText(newCommentDto.getText());
            comment.setCreated(DateUtils.now());
            comment.setEvent(event);
            comment.setAuthor(user);

            Comment savedComment = commentRepository.save(comment);
            if (!savedComment.equals(comment)) {
                log.warn("Can't add comment " + comment.getId());
                throw new ConflictException("Can't add comment " + comment.getId());
            }

            return CommentMapper.toCommentDto(savedComment);
        } else {
            log.warn(String.format("No confirmed requests for user %d, event %d!", user.getId(), event.getId()));
            throw new ConflictException(String.format("No confirmed requests for user %d, event %d!", user.getId(), event.getId()));
        }
    }

    public CommentDto changeComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        User user = userService.getUser(userId);

        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            Comment gotComment = comment.get();

            if (!gotComment.getAuthor().equals(user)) {
                log.warn("Can't change comment " + commentId + " (other author)");
                throw new ConflictException("Can't change comment " + commentId + " (other author)");
            }

            if (newCommentDto.getText().isBlank()) {
                log.warn("Text of comment is empty!");
                throw new ValidationException("Text of comment is empty!");
            }

            gotComment.setText(newCommentDto.getText());

            Comment savedComment = commentRepository.save(gotComment);
            if (!savedComment.equals(gotComment)) {
                log.warn("Can't add comment " + gotComment.getId());
                throw new ConflictException("Can't add comment " + gotComment.getId());
            }

            return CommentMapper.toCommentDto(savedComment);
        } else {
            log.warn("Not found comment " + commentId);
            throw new NotFoundException("Not found comment " + commentId);
        }
    }
}
