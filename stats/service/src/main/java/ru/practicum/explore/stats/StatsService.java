package ru.practicum.explore.stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.stats.model.App;
import ru.practicum.explore.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class StatsService {

    private final AppRepository appRepository;
    private final HitRepository hitRepository;

    public void addHit(HitDto hitDto) {

        App app = appRepository.findByName(hitDto.getApp());

        if (app == null) {
            app = appRepository.save(new App(0L, hitDto.getApp()));
        }
        Hit hit = new Hit();

        hit.setApp(app);
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());

        hitRepository.save(hit);
    }

    public Set<VisitDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique, int limit) {

        List<Hit> hits;

        if (uris.size() == 0) {
            hits = hitRepository.findByTimestampIsAfterAndTimestampIsBefore(start, end);
        } else {
            hits = hitRepository.findByTimestampIsAfterAndTimestampIsBeforeAndUriIn(start, end, uris);
        }

        int count = 0;
        List<VisitDto> visits = new ArrayList<>();

        for (Hit hit : hits) {
            VisitDto gotVisitDto = null;
            for (VisitDto visitDto : visits) {
                if (visitDto.getUri().equals(hit.getUri())) {
                    gotVisitDto = visitDto;
                    break;
                }
            }

            if (gotVisitDto == null) {
                VisitDto visitDto = new VisitDto();

                visitDto.setApp(hit.getApp().getName());
                visitDto.setUri(hit.getUri());
                visitDto.setHits(1);

                visits.add(visitDto);

                count++;
                if (count == limit) {
                    break;
                }
            } else {
                if (!unique) {
                    gotVisitDto.setHits(gotVisitDto.getHits() + 1);
                }
            }
        }

        return new TreeSet<>(visits);
    }
}
