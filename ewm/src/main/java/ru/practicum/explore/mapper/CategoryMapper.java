package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.CategoryDto;
import ru.practicum.explore.dto.NewCategoryDto;
import ru.practicum.explore.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {
    public static Category toCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();

        category.setName(newCategoryDto.getName());

        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static List<CategoryDto> toListCategoryDto(List<Category> categories) {
        List<CategoryDto> categoriesDto = new ArrayList<>();

        for (Category category : categories) {
            categoriesDto.add(toCategoryDto(category));
        }

        return categoriesDto;
    }
}
