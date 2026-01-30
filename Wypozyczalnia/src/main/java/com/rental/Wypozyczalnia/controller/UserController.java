package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Role;
import com.rental.Wypozyczalnia.model.User;
import com.rental.Wypozyczalnia.repository.RoleRepository;
import com.rental.Wypozyczalnia.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Zamiana roli na objekt do spring security
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Role.class, "roleRole", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    Role role = roleRepository.findById(text).orElse(null);
                    setValue(role);
                } else {
                    setValue(null);
                }
            }
        });
    }

    @GetMapping("/user")
    public String listUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer editId,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> user;

        if (search != null && !search.isBlank()) {
            user = userRepository.search(search, pageable);
        } else {
            user = userRepository.findAll(pageable);
        }

        model.addAttribute("user", user);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", user.getTotalPages());

        User formEntity;
        boolean showForm = false;

        if (editId != null) {
            formEntity = userRepository.findById(editId).orElse(new User());
            showForm = true;
        } else {
            formEntity = new User();
        }

        Set<String> allowedFields = Set.of("name", "email", "password", "roleRole");
        Map<String, String> formFields = new LinkedHashMap<>();

        for (java.lang.reflect.Field field : User.class.getDeclaredFields()) {
            if (!allowedFields.contains(field.getName())) continue;

            field.setAccessible(true);
            String inputType = "text";
            Class<?> type = field.getType();

            if (type.equals(Integer.class) || type.equals(int.class)) inputType = "number";
            else if (type.equals(Long.class) || type.equals(long.class)) inputType = "number";
            else if (type.equals(java.time.LocalDate.class)) inputType = "date";
            else if (type.equals(java.time.LocalDateTime.class)) inputType = "datetime-local";
            else if (type.equals(java.time.Instant.class)) inputType = "datetime-local";
            else if (type.equals(Boolean.class) || type.equals(boolean.class)) inputType = "checkbox";
            else if (type.equals(String.class)) {
                if (field.getName().toLowerCase().contains("email")) inputType = "email";
                else if (field.getName().toLowerCase().contains("password")) inputType = "password";
            }

            formFields.put(field.getName(), inputType);
        }

        Map<String, String> formValues = new LinkedHashMap<>();
        for (java.lang.reflect.Field field : User.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(formEntity);
                if (value != null) {
                    if (field.getName().equals("password")) {
                        formValues.put(field.getName(), "");
                    }
                    else if (value instanceof java.time.LocalDate ld) {
                        formValues.put(field.getName(), ld.toString());
                    } else if (value instanceof java.time.LocalDateTime ldt) {
                        formValues.put(field.getName(), ldt.toString());
                    } else if (value instanceof java.time.Instant instant) {
                        formValues.put(field.getName(), instant.toString());
                    } else if (value instanceof Role role) {
                        formValues.put(field.getName(), role.getRole() != null ? role.getRole() : "");
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
        Map<String, String> roleOptions = new LinkedHashMap<>();

        for (Role role : roleRepository.findAll()) {
            roleOptions.put(role.getRole(), role.getRole());
        }
        foreignKeyOptions.put("roleRole", roleOptions);

        model.addAttribute("formFields", formFields);
        model.addAttribute("formValues", formValues);
        model.addAttribute("formEntity", formEntity);
        model.addAttribute("showForm", showForm);
        model.addAttribute("foreignKeyOptions", foreignKeyOptions);

        return "user";
    }

    @PostMapping("/user")
    public String save(
            @Valid @ModelAttribute("formEntity") User user,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            Page<User> pageData = userRepository.findAll(PageRequest.of(0, 2));
            model.addAttribute("user", pageData);
            model.addAttribute("showForm", true);
            model.addAttribute("formEntity", user);

            Set<String> allowedFields = Set.of("name", "email", "password", "roleRole");
            Map<String, String> formFields = new LinkedHashMap<>();
            Map<String, String> formValues = new LinkedHashMap<>();

            for (java.lang.reflect.Field field : User.class.getDeclaredFields()) {
                if (!allowedFields.contains(field.getName())) continue;
                field.setAccessible(true);

                String inputType = "text";
                Class<?> type = field.getType();
                if (type.equals(Integer.class) || type.equals(int.class) || type.equals(Long.class) || type.equals(long.class)) inputType = "number";
                else if (type.equals(java.time.LocalDate.class)) inputType = "date";
                else if (type.equals(java.time.LocalDateTime.class) || type.equals(java.time.Instant.class)) inputType = "datetime-local";
                else if (type.equals(Boolean.class) || type.equals(boolean.class)) inputType = "checkbox";
                else if (type.equals(String.class)) {
                    if (field.getName().toLowerCase().contains("email")) inputType = "email";
                    else if (field.getName().toLowerCase().contains("password")) inputType = "password";
                }

                formFields.put(field.getName(), inputType);

                try {
                    Object value = field.get(user);
                    if (value != null) {
                        if (field.getName().equals("password")) {
                            formValues.put(field.getName(), "");
                        } else if (value instanceof Role role) {
                            formValues.put(field.getName(), role.getRole() != null ? role.getRole() : "");
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
            Map<String, String> roleOptions = new LinkedHashMap<>();
            for (Role role : roleRepository.findAll()) {
                roleOptions.put(role.getRole(), role.getRole());
            }
            foreignKeyOptions.put("roleRole", roleOptions);

            model.addAttribute("formFields", formFields);
            model.addAttribute("formValues", formValues);
            model.addAttribute("foreignKeyOptions", foreignKeyOptions);

            return "user";
        }

        if (user.getId() != null) {
            User existing = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existing.setName(user.getName());
            existing.setEmail(user.getEmail());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            existing.setRoleRole(user.getRoleRole());
            userRepository.save(existing);
        } else {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(user);
        }

        return "redirect:/user";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userRepository.deleteById(id);
        return "redirect:/user";
    }
}
