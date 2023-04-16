package ru.practicum.explore.stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class StatsService {

    private final HitRepository hitRepository;

    public void hit(HitDto hitDto) {

        Hit hit = hitRepository.save(HitMapper.toHit(hitDto));

        HitMapper.toHitDto(hit);
    }

    public Set<VisitDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        List<Hit> hits;

        if (uris.size() == 0) {
            hits = hitRepository.findByTimestampIsAfterAndTimestampIsBefore(start, end);
        } else {
            hits = hitRepository.findByTimestampIsAfterAndTimestampIsBeforeAndUriIn(start, end, uris);
        }

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

                visitDto.setApp(hit.getApp());
                visitDto.setUri(hit.getUri());
                visitDto.setHits(1);

                visits.add(visitDto);
            } else {
                if (!unique) {
                    gotVisitDto.setHits(gotVisitDto.getHits() + 1);
                }
            }
        }

        return new TreeSet<>(visits);
    }
}
