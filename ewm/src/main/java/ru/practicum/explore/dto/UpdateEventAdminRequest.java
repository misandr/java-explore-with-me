package ru.practicum.explore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.Constants;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private LocalDateTime eventDate;

    private Location location;

    private Integer participantLimit;

    private Boolean paid;

    private Boolean requestModeration;

    private String stateAction;
}
