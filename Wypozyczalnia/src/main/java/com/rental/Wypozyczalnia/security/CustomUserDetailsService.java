package com.rental.Wypozyczalnia.security;

import com.rental.Wypozyczalnia.model.User;
import com.rental.Wypozyczalnia.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("=== USER NOT FOUND: " + email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        return new CustomUserDetails(user);
    }
}
