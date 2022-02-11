package com.mrn.admin.user;

import com.mrn.admin.FileUploadUtil;
import com.mrn.admin.security.ShopmeUserDetails;
import com.mrn.common.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,
                              Model model) {
        String email = loggedUser.getUsername(); // here the username is user email
        User user = userService.getByEmail(email);

        model.addAttribute("user", user);

        return "/users/account_form";

    }

    @PostMapping("/account/update")
    public String saveDetails(User user, RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal ShopmeUserDetails loggedUser,
                              @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(
                    Objects.requireNonNull(multipartFile.getOriginalFilename()));

            user.setPhotos(fileName);
            User savedUser = userService.updateAccount(user);

            // name user photo directory
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir); // clean the directory where user photos are stored
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.updateAccount(user);
        }

        // update the logged user
        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());

        // use redirect attribute to add an attribute
        redirectAttributes.addFlashAttribute("message",
                "You account details have been updated successfully.");

        return "redirect:/account";
    }
}
