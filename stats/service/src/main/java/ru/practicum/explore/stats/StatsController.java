package ru.practicum.explore.stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@AllArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody HitDto hitDto) {
        log.info("Add hit: app {}, uri {}, ip {}, timestamp {}",
                hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
        statsService.addHit(hitDto);
    }

    @GetMapping(path = "/stats")
    public Set<VisitDto> getStats(@RequestParam @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime end,
                                  @RequestParam(defaultValue = "") List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get stats: start {}, end {}, uris {}, unique {}, ", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique, Constants.DEFAULT_COUNT_STATS_RECORDS);
    }
}
