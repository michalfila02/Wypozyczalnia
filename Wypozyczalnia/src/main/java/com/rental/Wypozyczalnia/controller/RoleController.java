package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Role;
import com.rental.Wypozyczalnia.model.Wypozyczalnie; // Zakładam import
import com.rental.Wypozyczalnia.repository.RoleRepository;
import com.rental.Wypozyczalnia.repository.WypozyczalnieRepository; // Zakładam import
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
public class RoleController {

    private final RoleRepository roleRepository;
    private final WypozyczalnieRepository wypozyczalnieRepository;

    public RoleController(RoleRepository roleRepository, WypozyczalnieRepository wypozyczalnieRepository) {
        this.roleRepository = roleRepository;
        this.wypozyczalnieRepository = wypozyczalnieRepository;
    }

    @GetMapping("/role")
    public String listRole(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String editId,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolePage;

        if (search != null && !search.isBlank()) {
            rolePage = roleRepository.search(search, pageable);
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        model.addAttribute("role", rolePage);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rolePage.getTotalPages());

        Role formEntity;
        boolean showForm = false;

        if (editId != null && !editId.isBlank()) {
            formEntity = roleRepository.findById(editId).orElse(new Role());
            showForm = true;
        } else {
            formEntity = new Role();
        }


        Set<String> allowedFields = Set.of("role", "wypozyczalnieNazwa");

        Map<String, String> formFields = new LinkedHashMap<>();

        for (java.lang.reflect.Field field : Role.class.getDeclaredFields()) {
            if (!allowedFields.contains(field.getName())) continue;

            field.setAccessible(true);
            String inputType = "text";
            formFields.put(field.getName(), inputType);
        }


        Map<String, String> formValues = new LinkedHashMap<>();
        for (java.lang.reflect.Field field : Role.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(formEntity);
                if (value != null) {
                    if (value instanceof Wypozyczalnie wyp) {
                        formValues.put(field.getName(), wyp.getNazwa());
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
        for (Wypozyczalnie w : wypozyczalnieRepository.findAll()) {

            wypozyczalnieOptions.put(w.getNazwa(), w.getNazwa());
        }
        foreignKeyOptions.put("wypozyczalnieNazwa", wypozyczalnieOptions);

        model.addAttribute("formFields", formFields);
        model.addAttribute("formValues", formValues);
        model.addAttribute("formEntity", formEntity);
        model.addAttribute("showForm", showForm);
        model.addAttribute("foreignKeyOptions", foreignKeyOptions);

        return "role";
    }

    @PostMapping("/role")
    public String save(
            @Valid @ModelAttribute("formEntity") Role roleEntity,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {

            Page<Role> pageData = roleRepository.findAll(PageRequest.of(0, 2));
            model.addAttribute("role", pageData);
            model.addAttribute("showForm", true);


            Set<String> allowedFields = Set.of("role", "wypozyczalnieNazwa");
            Map<String, String> formFields = new LinkedHashMap<>();
            Map<String, String> formValues = new LinkedHashMap<>();

            for (java.lang.reflect.Field field : Role.class.getDeclaredFields()) {
                if (!allowedFields.contains(field.getName())) continue;
                formFields.put(field.getName(), "text");

                try {
                    field.setAccessible(true);
                    Object value = field.get(roleEntity);
                    if (value instanceof Wypozyczalnie wyp) {
                        formValues.put(field.getName(), wyp.getNazwa());
                    } else {
                        formValues.put(field.getName(), value != null ? value.toString() : "");
                    }
                } catch (IllegalAccessException e) {
                    formValues.put(field.getName(), "");
                }
            }


            Map<String, Map<?, String>> foreignKeyOptions = new HashMap<>();
            Map<String, String> wypozyczalnieOptions = new LinkedHashMap<>();
            for (Wypozyczalnie w : wypozyczalnieRepository.findAll()) {
                wypozyczalnieOptions.put(w.getNazwa(), w.getNazwa());
            }
            foreignKeyOptions.put("wypozyczalnieNazwa", wypozyczalnieOptions);

            model.addAttribute("formFields", formFields);
            model.addAttribute("formValues", formValues);
            model.addAttribute("foreignKeyOptions", foreignKeyOptions);

            return "role";
        }

        roleRepository.save(roleEntity);

        return "redirect:/role";
    }

    @GetMapping("/role/delete/{id}")
    public String deleteRole(@PathVariable("id") String id) {
        roleRepository.deleteById(id);
        return "redirect:/role";
    }
}