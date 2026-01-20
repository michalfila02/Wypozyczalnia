package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Klienci;
import com.rental.Wypozyczalnia.repository.KlienciRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class KlienciController {

    private final KlienciRepository klienciRepository;

    public KlienciController(KlienciRepository klienciRepository) {
        this.klienciRepository = klienciRepository;
    }

    @GetMapping("/klienci")
    public String listKlienci(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer editId, // match entity ID type
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Klienci> klienci;

        if (search != null && !search.isBlank()) {
            klienci = klienciRepository.search(search, pageable);
        } else {
            klienci = klienciRepository.findAll(pageable);
        }

        model.addAttribute("klienci", klienci);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", klienci.getTotalPages());

        // ---------- POPUP FORM HANDLING ----------
        Klienci formEntity;
        boolean showForm = false;

        if (editId != null) {
            // Edit mode: load entity by ID
            formEntity = klienciRepository.findById(editId)
                    .orElse(new Klienci()); // fallback to new entity if not found
            showForm = true;
        } else {
            // Create mode: new entity, show form only when button clicked
            formEntity = new Klienci();
        }
        Map<String, String> formFields = new LinkedHashMap<>();
        Set<String> allowedFields = Set.of("imię", "nazwisko", "pesel"); // fields allowed in form

        for (java.lang.reflect.Field field : Klienci.class.getDeclaredFields()) {
            if (!allowedFields.contains(field.getName())) continue; // skip disallowed fields

            field.setAccessible(true);
            String inputType = "text";
            Class<?> type = field.getType();
            if (type.equals(Integer.class) || type.equals(int.class)) inputType = "number";
            else if (type.equals(java.time.LocalDate.class)) inputType = "date";
            else if (type.equals(Boolean.class) || type.equals(boolean.class)) inputType = "checkbox";
            else if (type.equals(String.class)) {
                if (field.getName().toLowerCase().contains("email")) inputType = "email";
            }

            formFields.put(field.getName(), inputType);
        }


        model.addAttribute("formFields", formFields);
        Map<String, String> formValues = new LinkedHashMap<>();

        for (java.lang.reflect.Field field : Klienci.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(formEntity); // get actual value
                if (value != null) {
                    // Special handling for LocalDate
                    if (value instanceof java.time.LocalDate ld) {
                        formValues.put(field.getName(), ld.toString());
                    } else {
                        formValues.put(field.getName(), value.toString());
                    }
                } else {
                    formValues.put(field.getName(), "");
                }
            } catch (IllegalAccessException ignored) {
                formValues.put(field.getName(), "");
            }
        }

        model.addAttribute("formValues", formValues);

        model.addAttribute("formEntity", formEntity);
        model.addAttribute("entity", formEntity); // required by Thymeleaf fragment
        model.addAttribute("showForm", showForm);

        return "klienci";
    }

    @PostMapping("/klienci")
    public String save(
            @Valid @ModelAttribute("formEntity") Klienci klienci,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            // reload first page with pagination to show fragment and validation errors
            Page<Klienci> pageData = klienciRepository.findAll(PageRequest.of(0, 2));
            model.addAttribute("klienci", pageData);
            model.addAttribute("showForm", true);
            model.addAttribute("formEntity", klienci);
            model.addAttribute("entity", klienci); // required for fragment
            return "klienci";
        }

        klienciRepository.save(klienci); // insert if id is null, update if exists
        return "redirect:/klienci";
    }
    @GetMapping("/klienci/delete/{id}")
    public String deleteKlient(@PathVariable("id") Integer id) {
        klienciRepository.deleteById(id);
        return "redirect:/klienci";
    }
}