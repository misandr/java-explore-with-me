package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Comment;
import ru.practicum.explore.model.Event;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEvent(Event event);
}
