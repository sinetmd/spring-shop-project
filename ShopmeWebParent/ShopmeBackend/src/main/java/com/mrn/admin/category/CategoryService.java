package com.mrn.admin.category;

import com.mrn.common.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // list all categories
    public List<Category> listAll(String sortDir) {
        Sort sort = Sort.by("name");

        // Ascending order used to be the default if not specified which order.
        if (sortDir == null || sortDir.isEmpty()) {
            sort = sort.ascending();
        } else if (sortDir.equals("asc")) { // if specified
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort.descending();
        }

        List<Category> rootCategories = categoryRepository.findRootCategories(sort);
        return listHierarchicalCategories(rootCategories, sortDir);
    }

    public List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {

        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                                               Category parent, int level, String sortDir) {

        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = level + 1;

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }
    }


    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());

        extracted(categoriesUsedInForm, categoriesInDB);

        return categoriesUsedInForm;
    }

    private void extracted(List<Category> formListCategory, Iterable<Category> dbCategoryList) {
        for (Category category : dbCategoryList) {
            // if this is a parent (has no parent)
            if (category.getParent() == null) {
                formListCategory.add(Category.copyIdAndName(category));

                // iterate through your children
                Set<Category> children = sortSubCategories(category.getChildren());

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

        Set<Category> children = sortSubCategories(parent.getChildren());

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
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "Duplicated Name";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "Duplicated Alias";
                }
            }
        } else { // here we are in editing mode (trying to edit a category and we might found that is not unique)
            if (categoryByName != null && !Objects.equals(categoryByName.getId(), id)) {
                return "Duplicated Name";
            }

            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if (categoryByAlias != null && !Objects.equals(categoryByAlias.getId(), id)) {
                return "Duplicated Alias";
            }
        }
        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>((cat1, cat2) -> {
            if (sortDir.equals("asc")) {
                return cat1.getName().compareTo(cat2.getName());
            } else {
                return cat2.getName().compareTo(cat1.getName());
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }

    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
        categoryRepository.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);

        if(countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }

        categoryRepository.deleteById(id);
    }

}
