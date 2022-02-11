package com.mrn.admin.category;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories/check_unique")
    public String checkUnique(@Param("id") Integer id,
                                @Param("name") String name, @Param("alias") String alias) {

        return categoryService.checkUnique(id, name, alias);
    }
}
