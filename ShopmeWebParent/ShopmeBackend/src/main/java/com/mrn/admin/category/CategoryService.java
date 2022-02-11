package com.mrn.admin.category;

import com.mrn.common.entity.Category;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // list all categories
    public List<Category> listAll() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return listHierarchicalCategories(rootCategories);
    }

    public List<Category> listHierarchicalCategories(List<Category> rootCategories) {

        List<Category> hierarchicalCategories = new ArrayList<>();

        for(Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = rootCategory.getChildren();

            for(Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                                                Category parent, int level) {

        Set<Category> children = parent.getChildren();
        int newSubLevel = level + 1;

        for(Category subCategory : children) {
            String name = "";
            for(int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel);
        }
    }


    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findAll();

        extracted(categoriesUsedInForm, categoriesInDB);

        return categoriesUsedInForm;
    }

    private void extracted(List<Category> formListCategory, Iterable<Category> dbCategoryList) {
        for (Category category : dbCategoryList) {
            // if this is a parent (has no parent)
            if (category.getParent() == null) {
                formListCategory.add(Category.copyIdAndName(category));

                // iterate through your children
                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    String categoryFormat = "--" + subCategory.getName();
                    formListCategory.add(Category.copyIdAndName(subCategory.getId(), categoryFormat));

                    listSubCategoriesUsedInForm(formListCategory, subCategory, 1);
                }
            }
        }
    }

    private void listSubCategoriesUsedInForm(List<Category> formListCategory, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;

        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String categoryFormat = "";

            for (int i = 0; i < newSubLevel; i++) {
                categoryFormat += "--";
            }

            // add the format text to the name of the new category added
            categoryFormat += subCategory.getName();

            // add in the list the new sub category
            formListCategory.add(Category.copyIdAndName(subCategory.getId(), categoryFormat));

            listSubCategoriesUsedInForm(formListCategory, subCategory, newSubLevel);
        }
    }

    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        }catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if(categoryByName != null) {
                return "Duplicated Name";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if(categoryByAlias != null) {
                    return "Duplicated Alias";
                }
            }
        } else { // here we are in editing mode (trying to edit a category and we might found that is not unique)
            if(categoryByName != null && !Objects.equals(categoryByName.getId(), id)) {
                return "Duplicated Name";
            }

            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if(categoryByAlias != null && !Objects.equals(categoryByAlias.getId(), id)) {
                return "Duplicated Alias";
            }
        }
        return "OK";
    }

}
