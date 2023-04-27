package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Compilation;


public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}
