package ru.practicum.explore.model;

import lombok.Data;

@Data
public class Location {
    private final Float lat;
    private final Float lon;

    private Location(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public static Location of(Float lat, Float lon) {
        return new Location(lat, lon);
    }
}
