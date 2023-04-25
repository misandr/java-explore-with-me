package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByIdAndInitiator(Long eventId, User initiator);
    List<Event> findByCategoryId(Long catId);
    Page<Event> findByInitiator(User initiator, Pageable pageable);

    Page<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsAfterAndEventDateIsBefore(List<Long> userIds, List<State> states, List<Long> categoriesIds, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Event> findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndEventDateIsBeforeAndPaidIsAndStateIs(String text1, String text2, State state, List<Long> categoriesIds, LocalDateTime start, LocalDateTime end, Boolean paid, Boolean onlyAvailable, Pageable pageable);

    Page<Event> findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndEventDateIsBeforeAndPaidIs(String text1, String text2, State state, List<Long> categoriesIds, LocalDateTime start, LocalDateTime end, Boolean paid, Pageable pageable);

    Page<Event> findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndPaidIsAndStateIs(String text1, String text2, State state, List<Long> categoriesIds, LocalDateTime time, Boolean paid, Boolean onlyAvailable, Pageable pageable);

    Page<Event> findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStateIsAndCategoryIdInAndEventDateIsAfterAndPaidIs(String text1, String text2, State state, List<Long> categoriesIds, LocalDateTime time, Boolean paid, Pageable pageable);
}
