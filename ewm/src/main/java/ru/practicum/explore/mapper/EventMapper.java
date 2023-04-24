package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.EventFullDto;
import ru.practicum.explore.dto.EventShortDto;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Location;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());

        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setState(event.getState());

        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));

        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setPublishedOn(event.getPublishedOn());

        eventFullDto.setLocation(Location.of(event.getLat(), event.getLon()));

        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setRequestModeration(event.getRequestModeration());

        eventFullDto.setParticipantLimit(event.getParticipantLimit());

        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setId(event.getId());

        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setAnnotation(event.getAnnotation());

        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));

        eventShortDto.setEventDate(event.getEventDate());

        eventShortDto.setPaid(event.getPaid());

        return eventShortDto;
    }

    public static List<EventShortDto> toListEventShortDto(List<Event> events) {
        List<EventShortDto> eventsShortDto = new ArrayList<>();

        for (Event event : events) {
            eventsShortDto.add(toEventShortDto(event));
        }

        return eventsShortDto;
    }

    public static List<EventFullDto> toListEventFullDto(List<Event> events) {
        List<EventFullDto> eventsFullDto = new ArrayList<>();

        for (Event event : events) {
            eventsFullDto.add(toEventFullDto(event));
        }

        return eventsFullDto;
    }
}
