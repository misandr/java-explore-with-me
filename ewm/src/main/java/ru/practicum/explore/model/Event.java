package ru.practicum.explore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.enums.State;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @Size(min = 3, max = 120)
    private String title;

    @JoinColumn(nullable = false)
    @Size(min = 20, max = 7000)
    private String description;

    @JoinColumn(nullable = false)
    @Size(min = 20, max = 2000)
    private String annotation;

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(nullable = false, name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(nullable = false, name = "initiator_id")
    private User initiator;

    @Column(name = "created_date")
    @JoinColumn(nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "event_date")
    @JoinColumn(nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "publish_date")
    private LocalDateTime publishedOn;

    @JoinColumn(nullable = false)
    private Float lat;
    @JoinColumn(nullable = false)
    private Float lon;

    @JoinColumn(nullable = false)
    private Boolean paid;

    @JoinColumn(nullable = false, name = "request_moderation")
    private Boolean requestModeration;

    @JoinColumn(nullable = false, name = "participant_limit")
    private Integer participantLimit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        return id != null && id.equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
