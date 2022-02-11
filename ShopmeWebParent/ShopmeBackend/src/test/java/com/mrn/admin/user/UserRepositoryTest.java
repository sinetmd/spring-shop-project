package com.mrn.admin.user;

import com.mrn.common.entity.Role;
import com.mrn.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 3);
        User userMe = new User("me@code.net", "12345", "Me", "MeMi");
        userMe.addRole(roleAdmin);

        User savedUser = repo.save(userMe);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRole() {
        User userBobo = new User("bobo@lulangiu.net", "fratecupetre", "Bobo", "Docta");
        Role roleAdmin = entityManager.find(Role.class, 3);
        Role roleAssistant = entityManager.find(Role.class, 7);

        userBobo.addRole(roleAdmin);
        userBobo.addRole(roleAssistant);

        User savedUser = repo.save(userBobo);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers () {
        Iterable<User> users = repo.findAll();
        users.forEach(System.out::println);
    }

    @Test
    public void testGetUserById() {
        Optional<User> user = repo.findById(1);

        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User user = repo.findById(1).get(); // get user

        user.setEmail("new@email.com");
        user.setEnabled(true);
        User savedUser = repo.save(user);

        assertThat(savedUser).isNotNull();
    }

    @Test
    public void testUpdateUserRoles() {
        // get the role from the user
        User user = repo.findById(1).get();

        //editor role
        Role assistant = new Role(7);
        // Admin role

        Role salesPerson = new Role(4);

        // remove the user role
        user.getRoles().remove(assistant);

        // add new sales person role to user
        user.addRole(salesPerson);

        repo.save(user);

    }

    @Test
    public void testDeleteUserById() {
        Integer id = 3;
        repo.deleteById(id);
    }

    // Test email
    @Test
    public void testGetUserByEmail() {
        String email = "mihg@gmail.com";
        User user = repo.getUserByEmail(email);

        assertThat(user).isNotNull();

    }

    @Test
    public void testCountById() {
        final Integer id = 11;
        Long countById = repo.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 5;
        // User user = repo.

        repo.updateEnableStatus(id, false);
    }

    @Test
    public void testEnableUser() {
        Integer id = 5;

        repo.updateEnableStatus(id, true);
    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 0;
        int pageSize = 4; // 4 elements in a page

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);

        List<User> listUsers = page.getContent();

        listUsers.forEach(System.out::println);

        assertThat(listUsers.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsers() {
        final String keyword = "bruce";

        int pageNumber = 0;
        int pageSize = 4; // 4 elements in a page

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(keyword, pageable);

        List<User> listUsers = page.getContent();

        listUsers.forEach(System.out::println);

        /*
        * If you search only by first name or last name
        * If you search by firstName OR lastName you'll find 2 results
        * */
        assertThat(listUsers.size()).isGreaterThan(0);
    }

}
