package com.mrn.admin.security;

import com.mrn.admin.user.UserRepository;
import com.mrn.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ShopmeUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userByEmail = userRepository.getUserByEmail(email);

        if (userByEmail != null) {
            return new ShopmeUserDetails(userByEmail);
        }
        throw new UsernameNotFoundException("Could not find user with email: " + email);
    }
}
