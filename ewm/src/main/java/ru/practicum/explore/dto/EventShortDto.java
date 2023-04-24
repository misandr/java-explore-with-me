package ru.practicum.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;

    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 2000)
    private String annotation;

    private CategoryDto category;
    private UserShortDto initiator;

    private LocalDateTime eventDate;

    private Boolean paid;

    private Long confirmedRequests;
    private Long views;
}
