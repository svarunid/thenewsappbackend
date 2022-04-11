package com.thenewsapp.service;

import com.thenewsapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pattern pattern = Pattern.compile("[a-z0-9]+@[a-z]+\\.[a-z]{2,3}");
        Matcher matcher = pattern.matcher(username);
        com.thenewsapp.user.User user;
        if(matcher.matches()) {
             user = userRepository
                     .findByEmail(username)
                     .orElseThrow(() -> new UsernameNotFoundException("Email: " + username + " not found"));
        } else {
            user = userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
        }

        return new User(user.getUsername(),user.getPassword(),new ArrayList<>());
    }
}
