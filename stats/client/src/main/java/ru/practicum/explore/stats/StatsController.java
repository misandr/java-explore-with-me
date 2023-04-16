package ru.practicum.explore.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsClient statsClient;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> hit(@RequestBody HitDto hitDto) {
        log.info("Add hit: app {}, uri {}, ip {}, timestamp {}",
                hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
        return statsClient.hit(hitDto);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(defaultValue = "") List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get stats: start {}, end {}, uris {}, unique {}, ", start, end, uris, unique);
        return statsClient.getStats(start, end, uris, unique);
    }
}
