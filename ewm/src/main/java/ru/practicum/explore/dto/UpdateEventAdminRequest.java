package ru.practicum.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.model.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    private String title;

    private String description;

    private String annotation;

    private Long category;

    private LocalDateTime eventDate;

    private Location location;

    private Integer participantLimit;

    private Boolean paid;

    private Boolean requestModeration;

    private String stateAction;
}
