package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.enums.RequestStatus;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.ParticipationRequest;
import ru.practicum.explore.model.User;

import java.util.List;


public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByIdIn(List<Long> requestIds);

    List<ParticipationRequest> findByEvent(Event event);

    List<ParticipationRequest> findByEventAndStatusIs(Event event, RequestStatus status);

    List<ParticipationRequest> findByRequester(User user);
}
