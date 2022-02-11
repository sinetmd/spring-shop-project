package com.mrn.admin.user;

import com.mrn.admin.FileUploadUtil;
import com.mrn.admin.export.UserCsvExporter;
import com.mrn.admin.export.UserPdfExporter;
import com.mrn.common.entity.Role;
import com.mrn.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listAll(Model model) {
/*
        List<User> listUsers = userService.listAll();
        model.addAttribute("listUsers", listUsers);

        return "users";
*/

        // return by pagination
        // sort by firstName from entity class
        // passing null to the keyword - by default I don't search for anyone
        return listByPage(1, model, "firstName", "asc", null);
    }

    // list users by paging
    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword) {

        System.out.println("Sort Field = " + sortField);
        System.out.println("Sort Dir = " + sortDir);

        Page<User> page = userService.listByPage(pageNum, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (long) (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount + UserService.USERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);

        // sort field
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        // reverse sort order
        model.addAttribute("reverseSortDir", reverseSortDir);

        // add keyword to the model
        model.addAttribute("keyword", keyword);

        return "/users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> roles = userService.listRoles();
        User user = new User();
        user.setEnabled(true); // checkbox enabled

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        //set title in user form (to create user)
        model.addAttribute("pageTitle", "Create new User");

        return "/users/user_form";
    }


    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(
                    Objects.requireNonNull(multipartFile.getOriginalFilename()));

            fileName = fileName.replaceAll(" ", "_").toLowerCase();

            user.setPhotos(fileName);
            User savedUser = userService.save(user);

            // name user photo directory
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir); // clean the directory where user photos are stored
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }

        // use redirect attribute to add an attribute
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

        return getRedirectURLToAffectedUser(user);
    }

    private String getRedirectURLToAffectedUser(User user) {
        // get the first part from email
        // tom@gmail.com = tom[0] -> '@' -> split part gmail.com[1]
        String firstPartOfEmail = user.getEmail().split("@")[0];

        // this will be redirected to the user that was         
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = userService.get(id);
            List<Role> roles = userService.listRoles();
            model.addAttribute("user", user);

            //set title in user form (to edit user)
            model.addAttribute("pageTitle", "Edit user (ID: " + id + ")");
            model.addAttribute("roles", roles);

            return "/users/user_form"; // move me to user form where i can edit it

        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users"; // redirect to users if user not found
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        // Because I'm on the users page I don't need to
        try {
            // delete user
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "User with "
                    + "[" + id + "]" + "has been successfully deleted!");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {

        userService.updateUserEnabledStatus(id, enabled);

        String status = enabled ? "enabled" : "disabled";
        final String message = "The user ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();

        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);
    }
}
