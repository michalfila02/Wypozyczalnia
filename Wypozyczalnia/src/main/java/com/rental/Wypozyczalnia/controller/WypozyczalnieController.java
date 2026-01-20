package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Wypozyczalnie;
import com.rental.Wypozyczalnia.repository.WypozyczalnieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WypozyczalnieController {

    private final WypozyczalnieRepository wypozyczalnieRepository;

    public WypozyczalnieController(WypozyczalnieRepository wypozyczalnieRepository) {
        this.wypozyczalnieRepository = wypozyczalnieRepository;
    }

    @GetMapping("/wypozyczalnie")
    public String listWypozyczalnie(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "2 ") int size, @RequestParam(required = false) String search,
                           Model model) {

        Page<Wypozyczalnie> wypozyczalnie;
        if (search != null && !search.isBlank()) {
            wypozyczalnie = wypozyczalnieRepository.search(
                    search,
                    PageRequest.of(page, 2)
            );
        } else {
            wypozyczalnie = wypozyczalnieRepository.findAll(
                    PageRequest.of(page, 2)
            );
        }

        model.addAttribute("wypozyczalnie", wypozyczalnie);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wypozyczalnie.getTotalPages());

        return "wypozyczalnie";
    }
}