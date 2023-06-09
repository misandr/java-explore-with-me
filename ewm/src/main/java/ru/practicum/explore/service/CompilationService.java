package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.*;
import ru.practicum.explore.exceptions.ConflictException;
import ru.practicum.explore.exceptions.NotFoundException;
import ru.practicum.explore.exceptions.ValidationException;
import ru.practicum.explore.mapper.CommentMapper;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.model.*;
import ru.practicum.explore.repository.CommentRepository;
import ru.practicum.explore.repository.CompilationEventsRepository;
import ru.practicum.explore.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationService {
    private final EventService eventService;
    private final CompilationRepository compilationRepository;
    private final CompilationEventsRepository compilationEventsRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {

        if (newCompilationDto.getTitle() == null) {
            log.warn("Compilation didn't save!");
            throw new ValidationException("Compilation didn't save!");
        }

        try {
            Compilation compilation = new Compilation();

            compilation.setTitle(newCompilationDto.getTitle());
            compilation.setPinned(newCompilationDto.getPinned());

            Compilation savedCompilation = compilationRepository.save(compilation);

            List<EventShortDto> events = new ArrayList<>();
            for (Long eventId : newCompilationDto.getEvents()) {
                Event gotEvent = eventService.getEvent(eventId);

                EventShortDto evenShortDto = EventMapper.toEventShortDto(gotEvent);

                List<Comment> comments = commentRepository.findByEvent(gotEvent);
                evenShortDto.setComments(CommentMapper.toCommentsDto(comments));

                events.add(evenShortDto);

                compilationEventsRepository.save(new CompilationEvent(0L, savedCompilation, gotEvent));
            }
            CompilationDto compilationDto = new CompilationDto();

            compilationDto.setId(savedCompilation.getId());
            compilationDto.setEvents(events);
            compilationDto.setTitle(savedCompilation.getTitle());
            compilationDto.setPinned(savedCompilation.getPinned());

            return compilationDto;
        } catch (RuntimeException e) {
            throw new ConflictException("Compilation didn't save!");
        }
    }

    @Transactional
    public CompilationDto changeCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isPresent()) {

            List<CompilationEvent> compilationEvents = compilationEventsRepository.findByCompilation(compilation.get());
            for (CompilationEvent compilationEvent : compilationEvents) {
                compilationEventsRepository.deleteById(compilationEvent.getId());
            }

            if (updateCompilationRequest.getTitle() != null) {
                compilation.get().setTitle(updateCompilationRequest.getTitle());
            }

            if (updateCompilationRequest.getPinned() != null) {
                compilation.get().setPinned(updateCompilationRequest.getPinned());
            }

            Compilation savedCompilation = compilationRepository.save(compilation.get());

            List<EventShortDto> events = new ArrayList<>();
            for (Long eventId : updateCompilationRequest.getEvents()) {
                Event gotEvent = eventService.getEvent(eventId);

                EventShortDto evenShortDto = EventMapper.toEventShortDto(gotEvent);

                List<Comment> comments = commentRepository.findByEvent(gotEvent);
                evenShortDto.setComments(CommentMapper.toCommentsDto(comments));

                events.add(evenShortDto);

                compilationEventsRepository.save(new CompilationEvent(0L, savedCompilation, gotEvent));
            }
            CompilationDto compilationDto = new CompilationDto();

            compilationDto.setId(savedCompilation.getId());
            compilationDto.setEvents(events);
            compilationDto.setTitle(savedCompilation.getTitle());
            compilationDto.setPinned(savedCompilation.getPinned());

            return compilationDto;

        } else {
            log.warn("Not found compilation " + compId);
            throw new NotFoundException("Not found compilation " + compId);
        }
    }

    @Transactional
    public CompilationDto getCompilation(Long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isPresent()) {

            List<CompilationEvent> compilationEvents = compilationEventsRepository.findByCompilation(compilation.get());
            for (CompilationEvent compilationEvent : compilationEvents) {
                compilationEventsRepository.deleteById(compilationEvent.getId());
            }

            List<EventShortDto> events = new ArrayList<>();
            for (CompilationEvent compilationEvent : compilationEvents) {
                EventShortDto evenShortDto = EventMapper.toEventShortDto(compilationEvent.getEvent());

                List<Comment> comments = commentRepository.findByEvent(compilationEvent.getEvent());
                evenShortDto.setComments(CommentMapper.toCommentsDto(comments));

                events.add(evenShortDto);
            }

            CompilationDto compilationDto = new CompilationDto();

            compilationDto.setId(compilation.get().getId());
            compilationDto.setEvents(events);
            compilationDto.setTitle(compilation.get().getTitle());
            compilationDto.setPinned(compilation.get().getPinned());

            return compilationDto;
        } else {
            log.warn("Not found compilation " + compId);
            throw new NotFoundException("Not found compilation " + compId);
        }
    }

    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, Range range) {
        int newFrom = range.getFrom() / range.getSize();
        Pageable page = PageRequest.of(newFrom, range.getSize());

        Page<Compilation> compilations = compilationRepository.findByPinned(pinned, page);

        List<CompilationDto> compilationsDto = new ArrayList<>();

        for (Compilation compilation : compilations.getContent()) {
            List<CompilationEvent> compilationEvents = compilationEventsRepository.findByCompilation(compilation);
            for (CompilationEvent compilationEvent : compilationEvents) {
                compilationEventsRepository.deleteById(compilationEvent.getId());
            }

            List<EventShortDto> events = new ArrayList<>();
            for (CompilationEvent compilationEvent : compilationEvents) {
                EventShortDto evenShortDto = EventMapper.toEventShortDto(compilationEvent.getEvent());

                List<Comment> comments = commentRepository.findByEvent(compilationEvent.getEvent());
                evenShortDto.setComments(CommentMapper.toCommentsDto(comments));

                events.add(evenShortDto);
            }

            CompilationDto compilationDto = new CompilationDto();

            compilationDto.setId(compilation.getId());
            compilationDto.setEvents(events);
            compilationDto.setTitle(compilation.getTitle());
            compilationDto.setPinned(compilation.getPinned());

            compilationsDto.add(compilationDto);
        }

        return compilationsDto;
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isPresent()) {
            List<CompilationEvent> compilationEvents = compilationEventsRepository.findByCompilation(compilation.get());

            for (CompilationEvent compilationEvent : compilationEvents) {
                compilationEventsRepository.deleteById(compilationEvent.getId());
            }

            compilationRepository.deleteById(compilation.get().getId());
        } else {
            log.warn("Not found compilation " + compId);
            throw new NotFoundException("Not found compilation " + compId);
        }
    }
}
