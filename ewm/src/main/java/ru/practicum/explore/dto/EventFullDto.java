package ru.practicum.explore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventFullDto {
    private Long id;

    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 7000)
    private String description;
    @Size(min = 20, max = 2000)
    private String annotation;

    private State state;

    private CategoryDto category;
    private UserShortDto initiator;

    private LocalDateTime createdOn;
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;

    private Location location;

    private Boolean paid;
    private Boolean requestModeration;

    private Integer participantLimit;

    private Long confirmedRequests;
    private Long views;
}
