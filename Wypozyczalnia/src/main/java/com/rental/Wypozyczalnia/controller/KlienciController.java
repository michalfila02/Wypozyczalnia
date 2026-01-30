package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Klienci;
import com.rental.Wypozyczalnia.repository.KlienciRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer editId,
            Model model) {

        model.addAttribute("search", search);

        Klienci formEntity;
        boolean showForm = false;

        if (editId != null) {
            formEntity = klienciRepository.findById(editId).orElse(new Klienci());
            showForm = true;
        } else {
            formEntity = new Klienci();
        }

        Set<String> allowedFields = Set.of(
                "imię",
                "nazwisko",
                "pesel"
        );

        Map<String, String> formFields = new LinkedHashMap<>();

        for (java.lang.reflect.Field field : Klienci.class.getDeclaredFields()) {
            if (!allowedFields.contains(field.getName())) continue;

            field.setAccessible(true);
            String inputType = "text";
            Class<?> type = field.getType();

            if (type.equals(Integer.class) || type.equals(int.class) ||
                    type.equals(Long.class) || type.equals(long.class)) {
                inputType = "number";
            } else if (type.equals(Double.class) || type.equals(double.class) ||
                    type.equals(java.math.BigDecimal.class)) {
                inputType = "number";
            }

            formFields.put(field.getName(), inputType);
        }

        Map<String, String> formValues = new LinkedHashMap<>();
        for (java.lang.reflect.Field field : Klienci.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(formEntity);
                if (value != null) {
                    formValues.put(field.getName(), value.toString());
                } else {
                    formValues.put(field.getName(), "");
                }
            } catch (IllegalAccessException ignored) {
                formValues.put(field.getName(), "");
            }
        }

        Map<String, Map<?, String>> foreignKeyOptions = new HashMap<>();

        model.addAttribute("formFields", formFields);
        model.addAttribute("formValues", formValues);
        model.addAttribute("formEntity", formEntity);
        model.addAttribute("showForm", showForm);
        model.addAttribute("foreignKeyOptions", foreignKeyOptions);

        return "klienci";
    }

    @PostMapping("/klienci")
    public String save(
            @Valid @ModelAttribute("formEntity") Klienci entity,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            Page<Klienci> pageData = klienciRepository.findAll(PageRequest.of(0, 2));
            model.addAttribute("klienci", pageData);
            model.addAttribute("showForm", true);

            Set<String> allowedFields = Set.of("imię", "nazwisko", "pesel");
            Map<String, String> formFields = new LinkedHashMap<>();
            Map<String, String> formValues = new LinkedHashMap<>();

            for (java.lang.reflect.Field field : Klienci.class.getDeclaredFields()) {
                if (!allowedFields.contains(field.getName())) continue;
                field.setAccessible(true);

                String inputType = "text";
                if (Number.class.isAssignableFrom(field.getType()) || field.getType().isPrimitive()) inputType = "number";

                formFields.put(field.getName(), inputType);

                try {
                    Object value = field.get(entity);
                    formValues.put(field.getName(), value != null ? value.toString() : "");
                } catch (IllegalAccessException e) {
                    formValues.put(field.getName(), "");
                }
            }

            Map<String, Map<?, String>> foreignKeyOptions = new HashMap<>();

            model.addAttribute("formFields", formFields);
            model.addAttribute("formValues", formValues);
            model.addAttribute("foreignKeyOptions", foreignKeyOptions);

            return "klienci";
        }

        klienciRepository.save(entity);
        return "redirect:/klienci";
    }

    @GetMapping("/klienci/delete/{id}")
    public String deleteKlient(@PathVariable("id") Integer id) {
        klienciRepository.deleteById(id);
        return "redirect:/klienci";
    }

    @GetMapping("/klienci/api/data")
    @ResponseBody
    public Page<Klienci> getKlienciData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.isBlank()) {
            return klienciRepository.search(search, pageable);
        } else {
            return klienciRepository.findAll(pageable);
        }
    }
}