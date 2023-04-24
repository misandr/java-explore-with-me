package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Compilation;
import ru.practicum.explore.model.CompilationEvent;

import java.util.List;


public interface CompilationEventsRepository extends JpaRepository<CompilationEvent, Long> {
    List<CompilationEvent> findByCompilation(Compilation compilation);
}
