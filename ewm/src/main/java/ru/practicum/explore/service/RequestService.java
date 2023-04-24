package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
            log.warn("Can't add request");
            throw new ConflictException("Can't add request");
        }

        if (event.getState() != State.PUBLISHED) {
            log.warn("Can't add request");
            throw new ConflictException("Can't add request");
        }

        List<ParticipationRequest> requests = requestRepository.findByEvent(event);

        if (requests.size() == event.getParticipantLimit()) {
            log.warn("Can't add request");
            throw new ConflictException("Can't add request");
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

        ParticipationRequest savedParticipationRequest = requestRepository.save(participationRequest);
        if (!savedParticipationRequest.equals(participationRequest)) {
            log.warn("Can't add request " + participationRequest.getId());
            throw new ConflictException("Can't add request " + participationRequest.getId());
        }

        return RequestMapper.toParticipationRequestDto(savedParticipationRequest);
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
                        List<ParticipationRequest> requestsForEvent = requestRepository.findByEvent(event);

                        if (requestsForEvent.size() == event.getParticipantLimit()) {
                            log.warn("Can't change request " + request.getId());
                            throw new ConflictException("Can't change request " + request.getId());
                        }

                        RequestStatus requestStatus;
                        if (!event.getRequestModeration() || (event.getParticipantLimit() == 0)) {
                            requestStatus = RequestStatus.CONFIRMED;
                        } else {
                            requestStatus = eventRequestStatusUpdateRequest.getStatus();
                        }

                        request.setStatus(requestStatus);

                        requestRepository.save(request);

                        if (requestStatus == RequestStatus.CONFIRMED) {
                            confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
                        } else if (requestStatus == RequestStatus.CANCELED) {
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

    public void canselRequest(Long userId, Long requestId) {
        User user = userService.getUser(userId);

        if (requestRepository.existsById(requestId)) {
            ParticipationRequest participationRequest = requestRepository.getReferenceById(requestId);

            if (participationRequest.getRequester().equals(user)) {
                participationRequest.setStatus(RequestStatus.CANCELED);

                requestRepository.save(participationRequest);
            }
        } else {
            log.warn("Not found request " + requestId);
            throw new NotFoundException("Not found request " + requestId);
        }
    }
}