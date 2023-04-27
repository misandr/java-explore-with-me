package ru.practicum.explore.stats;

import ru.practicum.explore.stats.model.Hit;

public class HitMapper {

    public static HitDto toHitDto(Hit hit) {
        return new HitDto(hit.getApp().getName(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }
}
