package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Klienci;
import com.rental.Wypozyczalnia.repository.KlienciRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KlienciController {

    private final KlienciRepository klienciRepository;

    public KlienciController(KlienciRepository klienciRepository) {
        this.klienciRepository = klienciRepository;
    }

    @GetMapping("/klienci")
    public String listKlienci(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "2") int size,
                              Model model) {

        Page<Klienci> klienci = klienciRepository.findAll(PageRequest.of(page, size));

        model.addAttribute("klienci", klienci);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", klienci.getTotalPages());

        return "klienci";
    }
}