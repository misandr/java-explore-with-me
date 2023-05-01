package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.DateUtils;
import ru.practicum.explore.dto.ParticipationRequestDto;
import ru.practicum.explore.enums.RequestStatus;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.exceptions.ConflictException;
import ru.practicum.explore.exceptions.NotFoundException;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.*;
import ru.practicum.explore.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    private final UserService userService;
    private final EventService eventService;
    private final RequestRepository requestRepository;

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userService.getUser(userId);
        Event event = eventService.getEvent(eventId);

        if (event.getInitiator().equals(user)) {
            log.warn("Can't add request (initiator = user)");
            throw new ConflictException("Can't add request (initiator = user)");
        }

        if (event.getState() != State.PUBLISHED) {
            log.warn("Can't add request (not published)");
            throw new ConflictException("Can't add request (not published)");
        }

        List<ParticipationRequest> requests = requestRepository.findByEventAndStatusIs(event, RequestStatus.CONFIRMED);
        if (requests.size() == event.getParticipantLimit()) {
            log.warn("Can't add request (limit)");
            throw new ConflictException("Can't add request (limit)");
        }

        ParticipationRequest participationRequest = new ParticipationRequest();

        participationRequest.setRequester(user);
        participationRequest.setEvent(event);
        participationRequest.setCreated(DateUtils.now());

        if (event.getRequestModeration()) {
            participationRequest.setStatus(RequestStatus.PENDING);
        } else {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        }

        try {
            ParticipationRequest savedParticipationRequest = requestRepository.save(participationRequest);
            return RequestMapper.toParticipationRequestDto(savedParticipationRequest);
        } catch (RuntimeException e) {
            log.warn("Can't add request " + participationRequest.getId());
            throw new ConflictException("Can't add request " + participationRequest.getId());
        }
    }

    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        User user = userService.getUser(userId);
        Event event = eventService.getEvent(eventId);

        List<ParticipationRequest> requests = requestRepository.findByEvent(event);
        List<ParticipationRequest> requestsOfUser = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (event.getInitiator().equals(user)) {
                requestsOfUser.add(request);
            }
        }

        return RequestMapper.toListParticipationRequestDto(requestsOfUser);
    }

    public EventRequestStatusUpdateResult changeStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<ParticipationRequest> requests = requestRepository.findByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            Event event = eventService.getEvent(eventId);

            if (request.getEvent().equals(event)) {
                User user = userService.getUser(userId);

                if (event.getInitiator().equals(user)) {

                    if (request.getStatus() == RequestStatus.PENDING) {

                        List<ParticipationRequest> requestsForEvent = requestRepository.findByEventAndStatusIs(event, RequestStatus.CONFIRMED);
                        if (requestsForEvent.size() == event.getParticipantLimit()) {
                            log.warn("Can't change request " + request.getId());
                            throw new ConflictException("Can't change request " + request.getId());
                        }

                        RequestStatus requestStatus;
                        if (!event.getRequestModeration() || (event.getParticipantLimit() == 0)) {
                            requestStatus = RequestStatus.CONFIRMED;
                        } else {
                            switch (eventRequestStatusUpdateRequest.getStatus()) {
                                case "CONFIRMED":
                                    requestStatus = RequestStatus.CONFIRMED;
                                    break;
                                case "REJECTED":
                                    requestStatus = RequestStatus.REJECTED;
                                    break;
                                case "PENDING":
                                    requestStatus = RequestStatus.PENDING;
                                    break;
                                default:
                                    log.warn("Can't change request " + request.getId());
                                    throw new ConflictException("Can't change request " + request.getId());
                            }

                        }

                        request.setStatus(requestStatus);

                        requestRepository.save(request);

                        if (requestStatus == RequestStatus.CONFIRMED) {
                            confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
                        } else if (requestStatus == RequestStatus.REJECTED) {
                            rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
                        }
                    } else {
                        log.warn("Can't change request " + request.getId());
                        throw new ConflictException("Can't change request " + request.getId());
                    }
                }
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    public List<ParticipationRequestDto> getOwnRequestsForOtherEvents(Long userId) {
        User user = userService.getUser(userId);
        List<ParticipationRequest> requests = requestRepository.findByRequester(user);

        return RequestMapper.toListParticipationRequestDto(requests);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userService.getUser(userId);

        if (requestRepository.existsById(requestId)) {
            ParticipationRequest participationRequest = requestRepository.getReferenceById(requestId);

            if (participationRequest.getRequester().equals(user)) {

                participationRequest.setStatus(RequestStatus.CANCELED);

                for (Comment comment : eventService.getCommentsByEvent(participationRequest.getEvent())) {
                    eventService.deleteComment(comment.getId());
                }

                return RequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
            } else {
                log.warn("Not found request " + requestId);
                throw new NotFoundException("Not found request " + requestId);
            }
        } else {
            log.warn("Not found request " + requestId);
            throw new NotFoundException("Not found request " + requestId);
        }
    }
}
