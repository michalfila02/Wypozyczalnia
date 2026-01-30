package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Wypozyczalnie;
import com.rental.Wypozyczalnia.repository.WypozyczalnieRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String editNazwa,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Wypozyczalnie> wypozyczalnie;

        if (search != null && !search.isBlank()) {
            wypozyczalnie = wypozyczalnieRepository.search(search, pageable);
        } else {
            wypozyczalnie = wypozyczalnieRepository.findAll(pageable);
        }

        model.addAttribute("wypozyczalnie", wypozyczalnie);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wypozyczalnie.getTotalPages());

        Wypozyczalnie formEntity;
        boolean showForm = false;

        if (editNazwa != null) {

            formEntity = wypozyczalnieRepository.findById(editNazwa)
                    .orElse(new Wypozyczalnie());
            showForm = true;
        } else {
            formEntity = new Wypozyczalnie();
        }

        Set<String> allowedFields = Set.of("nazwa", "miasto", "ulica", "nrUlicy", "telefonKontaktowy");
        Map<String, String> formFields = new LinkedHashMap<>();

        for (java.lang.reflect.Field field : Wypozyczalnie.class.getDeclaredFields()) {
            if (!allowedFields.contains(field.getName())) continue;

            field.setAccessible(true);
            String inputType = "text";
            Class<?> type = field.getType();

            if (type.equals(Integer.class) || type.equals(int.class)) inputType = "number";
            else if (type.equals(java.time.LocalDate.class)) inputType = "date";
            else if (type.equals(Boolean.class) || type.equals(boolean.class)) inputType = "checkbox";
            else if (type.equals(String.class)) {
                if (field.getName().toLowerCase().contains("email")) inputType = "email";
                else if (field.getName().toLowerCase().contains("telefon") || field.getName().toLowerCase().contains("phone")) inputType = "tel";
            }

            formFields.put(field.getName(), inputType);
        }

        Map<String, String> formValues = new LinkedHashMap<>();
        for (java.lang.reflect.Field field : Wypozyczalnie.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(formEntity);
                if (value != null) {
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

        model.addAttribute("formFields", formFields);
        model.addAttribute("formValues", formValues);
        model.addAttribute("formEntity", formEntity);
        model.addAttribute("showForm", showForm);
        model.addAttribute("foreignKeyOptions", null); // No foreign keys for Wypozyczalnie

        return "wypozyczalnie";
    }

    @PostMapping("/wypozyczalnie")
    public String save(
            @Valid @ModelAttribute("formEntity") Wypozyczalnie wypozyczalnie,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            Page<Wypozyczalnie> pageData = wypozyczalnieRepository.findAll(PageRequest.of(0, 2));
            model.addAttribute("wypozyczalnie", pageData);
            model.addAttribute("showForm", true);
            model.addAttribute("formEntity", wypozyczalnie);

            Set<String> allowedFields = Set.of("nazwa", "miasto", "ulica", "nrUlicy", "telefonKontaktowy");
            Map<String, String> formFields = new LinkedHashMap<>();
            Map<String, String> formValues = new LinkedHashMap<>();

            for (java.lang.reflect.Field field : Wypozyczalnie.class.getDeclaredFields()) {
                if (!allowedFields.contains(field.getName())) continue;
                field.setAccessible(true);

                String inputType = "text";
                Class<?> type = field.getType();
                if (type.equals(Integer.class) || type.equals(int.class)) inputType = "number";
                else if (type.equals(java.time.LocalDate.class)) inputType = "date";
                else if (type.equals(Boolean.class) || type.equals(boolean.class)) inputType = "checkbox";
                else if (type.equals(String.class)) {
                    if (field.getName().toLowerCase().contains("telefon")) inputType = "tel";
                }

                formFields.put(field.getName(), inputType);

                try {
                    Object value = field.get(wypozyczalnie);
                    formValues.put(field.getName(), value != null ? value.toString() : "");
                } catch (IllegalAccessException ignored) {
                    formValues.put(field.getName(), "");
                }
            }

            model.addAttribute("formFields", formFields);
            model.addAttribute("formValues", formValues);
            model.addAttribute("foreignKeyOptions", null);

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