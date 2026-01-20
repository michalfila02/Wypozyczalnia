package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Samochody;
import com.rental.Wypozyczalnia.repository.SamochodyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SamochodyController {

    private final SamochodyRepository samochodyRepository;

    public SamochodyController(SamochodyRepository samochodyRepository) {
        this.samochodyRepository = samochodyRepository;
    }

    @GetMapping("/samochody")
    public String listSamochody(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "2 ") int size, @RequestParam(required = false) String search,
                           Model model) {

        Page<Samochody> samochody;
        if (search != null && !search.isBlank()) {
            samochody = samochodyRepository.search(
                    search,
                    PageRequest.of(page, 2)
            );
        } else {
            samochody = samochodyRepository.findAll(
                    PageRequest.of(page, 2)
            );
        }

        model.addAttribute("samochody", samochody);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", samochody.getTotalPages());

        return "samochody";
    }
}