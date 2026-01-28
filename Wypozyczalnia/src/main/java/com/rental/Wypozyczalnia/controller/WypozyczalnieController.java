package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Wypozyczalnie;
import com.rental.Wypozyczalnia.repository.WypozyczalnieRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class WypozyczalnieController {

    private final WypozyczalnieRepository wypozyczalnieRepository;

    public WypozyczalnieController(WypozyczalnieRepository wypozyczalnieRepository) {
        this.wypozyczalnieRepository = wypozyczalnieRepository;
    }

    @GetMapping("/wypozyczalnie")
    public String listWypozyczalnie(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String editNazwa,
            Model model) {

        Page<Wypozyczalnie> wypozyczalnie = (search != null && !search.isBlank())
                ? wypozyczalnieRepository.search(search, PageRequest.of(page, size))
                : wypozyczalnieRepository.findAll(PageRequest.of(page, size));

        model.addAttribute("wypozyczalnie", wypozyczalnie);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wypozyczalnie.getTotalPages());

        Wypozyczalnie formEntity = (editNazwa != null)
                ? wypozyczalnieRepository.findById(editNazwa).orElse(new Wypozyczalnie())
                : new Wypozyczalnie();
        boolean showForm = editNazwa != null;

        Set<String> allowedFields = Set.of("nazwa", "adres", "telefon");
        Map<String, String> formFields = new LinkedHashMap<>();
        Map<String, String> formValues = new LinkedHashMap<>();

        for (var field : Wypozyczalnie.class.getDeclaredFields()) {
            if (!allowedFields.contains(field.getName())) continue;
            field.setAccessible(true);
            String inputType = "text";
            Class<?> type = field.getType();
            if (type.equals(Integer.class) || type.equals(int.class)) inputType = "number";
            else if (type.equals(java.time.LocalDate.class)) inputType = "date";
            else if (type.equals(Boolean.class) || type.equals(boolean.class)) inputType = "checkbox";

            formFields.put(field.getName(), inputType);

            try {
                Object value = field.get(formEntity);
                formValues.put(field.getName(), value != null ? value.toString() : "");
            } catch (IllegalAccessException ignored) {
                formValues.put(field.getName(), "");
            }
        }

        model.addAttribute("formFields", formFields);
        model.addAttribute("formValues", formValues);
        model.addAttribute("formEntity", formEntity);
        model.addAttribute("entity", formEntity);
        model.addAttribute("showForm", showForm);

        return "wypozyczalnie";
    }

    @PostMapping("/wypozyczalnie")
    public String save(@Valid @ModelAttribute("formEntity") Wypozyczalnie wypozyczalnie,
                       BindingResult result,
                       Model model) {

        if (result.hasErrors()) {
            Page<Wypozyczalnie> pageData = wypozyczalnieRepository.findAll(PageRequest.of(0, 2));
            model.addAttribute("wypozyczalnie", pageData);
            model.addAttribute("showForm", true);
            model.addAttribute("formEntity", wypozyczalnie);
            model.addAttribute("entity", wypozyczalnie);
            return "wypozyczalnie";
        }

        wypozyczalnieRepository.save(wypozyczalnie);
        return "redirect:/wypozyczalnie";
    }

    @GetMapping("/wypozyczalnie/delete/{nazwa}")
    public String deleteWypozyczalnia(@PathVariable("nazwa") String nazwa) {
        wypozyczalnieRepository.deleteById(nazwa);
        return "redirect:/wypozyczalnie";
    }
}