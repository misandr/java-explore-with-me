package ru.practicum.explore.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.stats.model.App;

public interface AppRepository extends JpaRepository<App, Long> {
    App findByName(String name);
}
