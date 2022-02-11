package com.mrn.admin.user;

import com.mrn.common.entity.Role;
import com.mrn.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public static final int USERS_PER_PAGE = 4;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAll() {
        return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
    }

    public User getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }


    public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, USERS_PER_PAGE, sort);

        if (keyword != null) {
            return userRepository.findAll(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    // User from the form to be updated
    public User save(User user) {

        // check if user is in updating mode
        boolean isUpdatingUser = (user.getId() != null);

        if (isUpdatingUser) {
            // get existing user from db
            User existingUser = userRepository.findById(user.getId()).get();

            // if the user password is empty in the form
            // means the user wants to keep the old password
            if (user.getEmail().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // encode the password introduced by user
                encodePassword(user);
            }
        } else {
            encodePassword(user); // encode password of user
        }

        return userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);

        if (userByEmail == null) return true; // because no user has been created

        // If is null it means I'm the in create form
        // otherwise it means I'm in the edit form
        boolean isCreatingNew = (id == null);

        // This happens when I create a new user
        if (isCreatingNew) {
            // if this email exists then is not unique
            if (userByEmail != null) return false;
        } else {
            // if my user has an email that coincides with others user email
            // then is not unique
            if (userByEmail.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return (userRepository.findById(id).get());
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }

    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);

        // check if id exists
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not found any user with ID: " + id);
        }

        userRepository.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnableStatus(id, enabled);
    }

    public User updateAccount(User user) {

        User userInDb = userRepository.findById(user.getId()).get();

        if(!user.getPassword().isEmpty()) {
            userInDb.setPassword(user.getPassword());
            encodePassword(userInDb);
        }

        if(user.getPhotos() != null) {
            userInDb.setPhotos(user.getPhotos());
        }

        userInDb.setFirstName(user.getFirstName());
        userInDb.setLastName(user.getLastName());

        return userRepository.save(userInDb);
    }
}
