package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.User;
import com.rental.Wypozyczalnia.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public String listUser(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "2") int size, @RequestParam(required = false) String search,
                           Model model) {

        Page<User> user;
        if (search != null && !search.isBlank()) {
            user = userRepository.search(
                    search,
                    PageRequest.of(page, 2)
            );
        } else {
            user = userRepository.findAll(
                    PageRequest.of(page, 2)
            );
        }

        model.addAttribute("user", user);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", user.getTotalPages());

        return "user";
    }
}