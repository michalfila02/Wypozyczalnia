package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Samochody;
import com.rental.Wypozyczalnia.model.Wypozyczalnie;
import com.rental.Wypozyczalnia.model.Role;
import com.rental.Wypozyczalnia.repository.SamochodyRepository;
import com.rental.Wypozyczalnia.repository.WypozyczalnieRepository;
import com.rental.Wypozyczalnia.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class SamochodyController {

    private final SamochodyRepository samochodyRepository;
    private final WypozyczalnieRepository wypozyczalnieRepository;
    private final RoleRepository roleRepository;

    public SamochodyController(SamochodyRepository samochodyRepository,
                               WypozyczalnieRepository wypozyczalnieRepository,
                               RoleRepository roleRepository) {
        this.samochodyRepository = samochodyRepository;
        this.wypozyczalnieRepository = wypozyczalnieRepository;
        this.roleRepository = roleRepository;
    }

    private Wypozyczalnie getUserWypozyczalnia() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;

        return auth.getAuthorities().stream()
                .map(authority -> {
                    String roleName = authority.getAuthority().replace("ROLE_", "");
                    return roleRepository.findById(roleName).orElse(null);
                })
                .filter(role -> role != null && role.getWypozyczalnieNazwa() != null)
                .map(Role::getWypozyczalnieNazwa)
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/samochody")
    public String listSamochody(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String editId,
            Model model) {

        Wypozyczalnie userWypozyczalnia = getUserWypozyczalnia();

        Pageable pageable = PageRequest.of(page, size);
        Page<Samochody> samochody;

        if (search != null && !search.isBlank()) {
            samochody = samochodyRepository.search(search, pageable);
        } else {
            samochody = samochodyRepository.findAll(pageable);
        }

        if (userWypozyczalnia != null) {
            List<Samochody> filteredList = samochody.getContent().stream()
                    .filter(s -> s.getWypozyczalnieNazwa() != null &&
                            s.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa()))
                    .collect(Collectors.toList());
            samochody = new PageImpl<>(filteredList, pageable, filteredList.size());
        }

        model.addAttribute("samochody", samochody);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", samochody.getTotalPages());

        Samochody formEntity;
        boolean showForm = false;

        if (editId != null && !editId.isBlank()) {
            formEntity = samochodyRepository.findById(editId).orElse(new Samochody());
            if (userWypozyczalnia != null && formEntity.getWypozyczalnieNazwa() != null) {
                if (!formEntity.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa())) {
                    return "redirect:/samochody";
                }
            }
            showForm = true;
        } else {
            formEntity = new Samochody();
        }

        Set<String> allowedFields = Set.of(
                "nrRejestracyjny",
                "marka",
                "model",
                "kosztWynajeciaNaDzien",
                "przebiegWKm",
                "wypozyczalnieNazwa"
        );

        Map<String, String> formFields = new LinkedHashMap<>();

        for (java.lang.reflect.Field field : Samochody.class.getDeclaredFields()) {
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
        for (java.lang.reflect.Field field : Samochody.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(formEntity);
                if (value != null) {
                    if (value instanceof Wypozyczalnie w) {
                        formValues.put(field.getName(), w.getNazwa());
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

        Map<String, Map<?, String>> foreignKeyOptions = new HashMap<>();
        Map<String, String> wypozyczalnieOptions = new LinkedHashMap<>();

        if (userWypozyczalnia != null) {
            wypozyczalnieOptions.put(userWypozyczalnia.getNazwa(), userWypozyczalnia.getNazwa());
        } else {
            for (Wypozyczalnie w : wypozyczalnieRepository.findAll()) {
                wypozyczalnieOptions.put(w.getNazwa(), w.getNazwa());
            }
        }
        foreignKeyOptions.put("wypozyczalnieNazwa", wypozyczalnieOptions);

        model.addAttribute("formFields", formFields);
        model.addAttribute("formValues", formValues);
        model.addAttribute("formEntity", formEntity);
        model.addAttribute("showForm", showForm);
        model.addAttribute("foreignKeyOptions", foreignKeyOptions);

        return "samochody";
    }

    @PostMapping("/samochody")
    public String save(
            @Valid @ModelAttribute("formEntity") Samochody entity,
            BindingResult result,
            Model model) {

        Wypozyczalnie userWypozyczalnia = getUserWypozyczalnia();

        if (userWypozyczalnia != null && entity.getWypozyczalnieNazwa() != null) {
            if (!entity.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa())) {
                return "redirect:/samochody";
            }
        }

        if (result.hasErrors()) {
            Page<Samochody> pageData = samochodyRepository.findAll(PageRequest.of(0, 2));
            model.addAttribute("samochody", pageData);
            model.addAttribute("showForm", true);

            Set<String> allowedFields = Set.of("nrRejestracyjny", "marka", "model", "kosztWynajeciaNaDzien", "przebiegWKm", "wypozyczalnieNazwa");
            Map<String, String> formFields = new LinkedHashMap<>();
            Map<String, String> formValues = new LinkedHashMap<>();

            for (java.lang.reflect.Field field : Samochody.class.getDeclaredFields()) {
                if (!allowedFields.contains(field.getName())) continue;
                field.setAccessible(true);

                String inputType = "text";
                if (Number.class.isAssignableFrom(field.getType()) || field.getType().isPrimitive()) inputType = "number";

                formFields.put(field.getName(), inputType);

                try {
                    Object value = field.get(entity);
                    if (value instanceof Wypozyczalnie w) {
                        formValues.put(field.getName(), w.getNazwa());
                    } else {
                        formValues.put(field.getName(), value != null ? value.toString() : "");
                    }
                } catch (IllegalAccessException e) {
                    formValues.put(field.getName(), "");
                }
            }

            Map<String, Map<?, String>> foreignKeyOptions = new HashMap<>();
            Map<String, String> wypozyczalnieOptions = new LinkedHashMap<>();

            if (userWypozyczalnia != null) {
                wypozyczalnieOptions.put(userWypozyczalnia.getNazwa(), userWypozyczalnia.getNazwa());
            } else {
                for (Wypozyczalnie w : wypozyczalnieRepository.findAll()) {
                    wypozyczalnieOptions.put(w.getNazwa(), w.getNazwa());
                }
            }
            foreignKeyOptions.put("wypozyczalnieNazwa", wypozyczalnieOptions);

            model.addAttribute("formFields", formFields);
            model.addAttribute("formValues", formValues);
            model.addAttribute("foreignKeyOptions", foreignKeyOptions);

            return "samochody";
        }

        samochodyRepository.save(entity);
        return "redirect:/samochody";
    }

    @GetMapping("/samochody/delete/{id}")
    public String deleteSamochody(@PathVariable("id") String id) {
        Wypozyczalnie userWypozyczalnia = getUserWypozyczalnia();

        if (userWypozyczalnia != null) {
            Samochody samochod = samochodyRepository.findById(id).orElse(null);
            if (samochod != null && samochod.getWypozyczalnieNazwa() != null) {
                if (!samochod.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa())) {
                    return "redirect:/samochody";
                }
            }
        }

        samochodyRepository.deleteById(id);
        return "redirect:/samochody";
    }
}