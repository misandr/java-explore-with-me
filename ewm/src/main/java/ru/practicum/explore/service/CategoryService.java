package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.CategoryDto;
import ru.practicum.explore.dto.NewCategoryDto;
import ru.practicum.explore.exceptions.ConflictException;
import ru.practicum.explore.exceptions.ConflictValueException;
import ru.practicum.explore.exceptions.NotFoundException;
import ru.practicum.explore.exceptions.ValidationException;
import ru.practicum.explore.mapper.CategoryMapper;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Range;
import ru.practicum.explore.repository.CategoryRepository;
import ru.practicum.explore.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName() == null) {
            log.warn("Category didn't save!");
            throw new ConflictValueException("Category didn't save!");
        }

        try {
            Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));

            return CategoryMapper.toCategoryDto(category);
        } catch (RuntimeException e) {
            throw new ConflictException("Category didn't save!");
        }
    }

    public List<CategoryDto> getCategories(Range range) {
        int newFrom = range.getFrom() / range.getSize();
        Pageable page = PageRequest.of(newFrom, range.getSize());

        Page<Category> categoriesPage = categoryRepository.findAll(page);

        return CategoryMapper.toListCategoryDto(categoriesPage.getContent());
    }

    public CategoryDto getCategoryDto(Long catId) {
        return CategoryMapper.toCategoryDto(getCategory(catId));
    }

    public Category getCategory(Long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isPresent()) {
            return category.get();
        } else {
            log.warn("Not found category " + catId);
            throw new NotFoundException("Not found category " + catId);
        }
    }

    public CategoryDto changeCategory(Long catId, NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsById(catId)) {
            Category gotCategory = categoryRepository.getReferenceById(catId);

            if (newCategoryDto.getName() != null) {
                gotCategory.setName(newCategoryDto.getName());

                try {
                    Category category = categoryRepository.save(gotCategory);

                    return CategoryMapper.toCategoryDto(category);
                } catch (RuntimeException e) {
                    log.warn("Category didn't change " + catId);
                    throw new ConflictException("Category didn't change " + catId);
                }
            } else {
                log.warn("Category didn't change " + catId);
                throw new ValidationException("Category didn't change " + catId);
            }
        } else {
            log.warn("Not found category " + catId);
            throw new NotFoundException("Not found category " + catId);
        }
    }

    public void deleteCategory(Long catId) {
        if (categoryRepository.existsById(catId)) {

            List<Event> events = eventRepository.findByCategoryId(catId);

            if (events.size() == 0) {
                categoryRepository.deleteById(catId);
            } else {
                log.warn("Category didn't delete!");
                throw new ConflictException("Category didn't delete!");
            }
        } else {
            log.warn("Not found category " + catId);
            throw new NotFoundException("Not found category " + catId);
        }
    }
}
