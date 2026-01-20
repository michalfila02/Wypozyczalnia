package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Role;
import com.rental.Wypozyczalnia.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping("/role")
    public String listRole(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "2 ") int size, @RequestParam(required = false) String search,
                              Model model) {

        Page<Role> role;
        if (search != null && !search.isBlank()) {
            role = roleRepository.search(
                    search,
                    PageRequest.of(page, 2)
            );
        } else {
            role = roleRepository.findAll(
                    PageRequest.of(page, 2)
            );
        }

        model.addAttribute("role", role);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", role.getTotalPages());

        return "role";
    }
}