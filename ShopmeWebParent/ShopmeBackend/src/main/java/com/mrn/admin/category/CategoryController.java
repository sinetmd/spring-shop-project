package com.mrn.admin.category;

import com.mrn.admin.FileUploadUtil;
import com.mrn.common.entity.Category;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listAll(Model model) {

        List<Category> listCategories = categoryService.listAll();

        model.addAttribute("listCategories", listCategories);
        return "/categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        final String pageTitle = "Create New Category";

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", pageTitle);

        return "/categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {

        if(!multipartFile.isEmpty()) {

            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            fileName = fileName.replaceAll(" ", "_");

            category.setImage(fileName);

            Category savedCategory = categoryService.save(category);

            // the upload directory is outside the module
            String uploadDir = "category-images/" + savedCategory.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            categoryService.save(category);
        }

        final String message = "The category has been successfully saved.";
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name="id")
                                           Integer id, Model model, RedirectAttributes redirectAttributes) {

        final String pageTitle = "Edit Category (ID: " + id + ")";

        try {
            Category category = categoryService.get(id);

            List<Category> listCategories = categoryService.listCategoriesUsedInForm();


            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", pageTitle);

            return "/categories/category_form";

        }catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }
}
