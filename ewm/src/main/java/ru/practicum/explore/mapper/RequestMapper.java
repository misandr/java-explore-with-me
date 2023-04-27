package ru.practicum.explore.mapper;


import ru.practicum.explore.dto.ParticipationRequestDto;
import ru.practicum.explore.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;


public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();

        participationRequestDto.setId(participationRequest.getId());

        participationRequestDto.setRequester(participationRequest.getRequester().getId());
        participationRequestDto.setEvent(participationRequest.getEvent().getId());
        participationRequestDto.setCreated(participationRequest.getCreated());
        participationRequestDto.setStatus(participationRequest.getStatus());

        return participationRequestDto;
    }

    public static List<ParticipationRequestDto> toListParticipationRequestDto(List<ParticipationRequest> participationRequests) {
        List<ParticipationRequestDto> participationRequestsDto = new ArrayList<>();

        for (ParticipationRequest participationRequest : participationRequests) {
            participationRequestsDto.add(toParticipationRequestDto(participationRequest));
        }

        return participationRequestsDto;
    }
}
