package ru.practicum.explore.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    List<Hit> findByTimestampIsAfterAndTimestampIsBefore(LocalDateTime startTime, LocalDateTime endTime);

    List<Hit> findByTimestampIsAfterAndTimestampIsBeforeAndUriIn(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

}
