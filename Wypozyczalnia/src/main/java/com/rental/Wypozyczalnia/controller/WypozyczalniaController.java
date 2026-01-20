package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Wypozyczalnia;
import com.rental.Wypozyczalnia.repository.WypozyczalniaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WypozyczalniaController {

    private final WypozyczalniaRepository wypozyczalniaRepository;

    public WypozyczalniaController(WypozyczalniaRepository wypozyczalniaRepository) {
        this.wypozyczalniaRepository = wypozyczalniaRepository;
    }

    @GetMapping("/wypozyczalnia")
    public String listWypozyczalnia(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "2 ") int size, @RequestParam(required = false) String search,
                           Model model) {

        Page<Wypozyczalnia> wypozyczalnia;
        if (search != null && !search.isBlank()) {
            wypozyczalnia = wypozyczalniaRepository.search(
                    search,
                    PageRequest.of(page, 2)
            );
        } else {
            wypozyczalnia = wypozyczalniaRepository.findAll(
                    PageRequest.of(page, 2)
            );
        }

        model.addAttribute("wypozyczalnia", wypozyczalnia);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wypozyczalnia.getTotalPages());

        return "wypozyczalnia";
    }
}