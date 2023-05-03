package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Comment;
import ru.practicum.explore.model.Event;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEvent(Event event);

    Page<Comment> findByAuthorIdInAndEventIdIn(List<Long> userIds, List<Long> eventIds, Pageable pageable);

    Page<Comment> findByEventIdIn(List<Long> eventIds, Pageable pageable);

    Page<Comment> findByAuthorIdIn(List<Long> userIds, Pageable pageable);
}
