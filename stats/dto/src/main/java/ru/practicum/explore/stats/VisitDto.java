package ru.practicum.explore.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitDto implements Comparable<VisitDto> {
    private String app;
    private String uri;
    private long hits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitDto visitDto = (VisitDto) o;
        return Objects.equals(app, visitDto.app) &&
                Objects.equals(uri, visitDto.uri);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (app != null) {
            hash = hash + app.hashCode();
        }
        hash = hash * 31;

        if (uri != null) {
            hash = hash + uri.hashCode();
        }

        return hash;
    }

    @Override
    public int compareTo(VisitDto visitDto) {

        if (hits < visitDto.getHits()) {
            return 1;
        } else if (hits > visitDto.getHits()) {
            return -1;
        } else {
            return uri.compareTo(visitDto.getUri());
        }
    }
}
