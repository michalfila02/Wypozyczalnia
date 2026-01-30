package com.rental.Wypozyczalnia.controller;

import com.rental.Wypozyczalnia.model.Wypozyczalnia;
import com.rental.Wypozyczalnia.model.Klienci;
import com.rental.Wypozyczalnia.model.Samochody;
import com.rental.Wypozyczalnia.model.Wypozyczalnie;
import com.rental.Wypozyczalnia.model.Role;
import com.rental.Wypozyczalnia.repository.WypozyczalniaRepository;
import com.rental.Wypozyczalnia.repository.KlienciRepository;
import com.rental.Wypozyczalnia.repository.SamochodyRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WypozyczalniaController {

    private final WypozyczalniaRepository wypozyczalniaRepository;
    private final KlienciRepository klienciRepository;
    private final SamochodyRepository samochodyRepository;
    private final RoleRepository roleRepository;

    public WypozyczalniaController(WypozyczalniaRepository wypozyczalniaRepository,
                                   KlienciRepository klienciRepository,
                                   SamochodyRepository samochodyRepository,
                                   RoleRepository roleRepository) {
        this.wypozyczalniaRepository = wypozyczalniaRepository;
        this.klienciRepository = klienciRepository;
        this.samochodyRepository = samochodyRepository;
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

    @GetMapping("/wypozyczalnia")
    public String listWypozyczalnia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer editId,
            Model model) {

        Wypozyczalnie userWypozyczalnia = getUserWypozyczalnia();

        long liczbaKlientow = klienciRepository.count();
        long liczbaSamochodow = samochodyRepository.count();

        if (liczbaKlientow == 0 || liczbaSamochodow == 0) {
            model.addAttribute("systemBlocked", true);
            model.addAttribute("blockReason",
                    (liczbaKlientow == 0 ? "Brak zarejestrowanych klientów. " : "") +
                            (liczbaSamochodow == 0 ? "Brak dostępnych samochodów." : ""));
            return "wypozyczalnia";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Wypozyczalnia> wypPage;
        if (search != null && !search.isBlank()) {
            wypPage = wypozyczalniaRepository.search(search, pageable);
        } else {
            wypPage = wypozyczalniaRepository.findAll(pageable);
        }

        if (userWypozyczalnia != null) {
            List<Wypozyczalnia> filteredList = wypPage.getContent().stream()
                    .filter(w -> w.getSamochodyNrRejestracyjny() != null &&
                            w.getSamochodyNrRejestracyjny().getWypozyczalnieNazwa() != null &&
                            w.getSamochodyNrRejestracyjny().getWypozyczalnieNazwa().getNazwa()
                                    .equals(userWypozyczalnia.getNazwa()))
                    .collect(Collectors.toList());
            wypPage = new PageImpl<>(filteredList, pageable, filteredList.size());
        }

        model.addAttribute("wypozyczalnia", wypPage);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wypPage.getTotalPages());


        Wypozyczalnia formEntity = (editId != null)
                ? wypozyczalniaRepository.findById(editId).orElse(new Wypozyczalnia())
                : new Wypozyczalnia();

        if (editId != null && userWypozyczalnia != null && formEntity.getSamochodyNrRejestracyjny() != null) {
            Samochody samochod = formEntity.getSamochodyNrRejestracyjny();
            if (samochod.getWypozyczalnieNazwa() != null &&
                    !samochod.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa())) {
                return "redirect:/wypozyczalnia"; // No access
            }
        }

        model.addAttribute("formEntity", formEntity);
        model.addAttribute("showForm", editId != null);


        Set<String> allowedFields = Set.of("idKlienta", "dataWypożyczenia", "dataOddania", "samochodyNrRejestracyjny");
        Map<String, String> formFields = new LinkedHashMap<>();
        Map<String, String> formValues = new LinkedHashMap<>();

        for (java.lang.reflect.Field field : Wypozyczalnia.class.getDeclaredFields()) {
            if (!allowedFields.contains(field.getName())) continue;
            field.setAccessible(true);

            String inputType = field.getType().equals(java.time.LocalDate.class) ? "date" : "text";
            if (Number.class.isAssignableFrom(field.getType()) || field.getType().isPrimitive()) inputType = "number";
            formFields.put(field.getName(), inputType);

            try {
                Object val = field.get(formEntity);
                if (val instanceof Klienci k) formValues.put(field.getName(), String.valueOf(k.getId()));
                else if (val instanceof Samochody s) formValues.put(field.getName(), s.getNrRejestracyjny());
                else formValues.put(field.getName(), val != null ? val.toString() : "");
            } catch (Exception e) { formValues.put(field.getName(), ""); }
        }


        Map<String, Map<?, String>> foreignKeyOptions = new HashMap<>();

        Map<Integer, String> klientOpts = new LinkedHashMap<>();
        klienciRepository.findAll().forEach(k -> klientOpts.put(k.getId(), k.getId() + ". " + k.getImię() + " " + k.getNazwisko()));

        Map<String, String> autoOpts = new LinkedHashMap<>();

        if (userWypozyczalnia != null) {
            samochodyRepository.findAll().stream()
                    .filter(s -> s.getWypozyczalnieNazwa() != null &&
                            s.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa()))
                    .forEach(s -> autoOpts.put(s.getNrRejestracyjny(), s.getMarka() + " " + s.getModel()));
        } else {
            samochodyRepository.findAll().forEach(s -> autoOpts.put(s.getNrRejestracyjny(), s.getMarka() + " " + s.getModel()));
        }

        foreignKeyOptions.put("idKlienta", klientOpts);
        foreignKeyOptions.put("samochodyNrRejestracyjny", autoOpts);

        model.addAttribute("formFields", formFields);
        model.addAttribute("formValues", formValues);
        model.addAttribute("foreignKeyOptions", foreignKeyOptions);

        return "wypozyczalnia";
    }

    @PostMapping("/wypozyczalnia")
    public String save(@Valid @ModelAttribute("formEntity") Wypozyczalnia entity, BindingResult result) {
        Wypozyczalnie userWypozyczalnia = getUserWypozyczalnia();

        if (userWypozyczalnia != null && entity.getSamochodyNrRejestracyjny() != null) {
            Samochody samochod = samochodyRepository.findById(entity.getSamochodyNrRejestracyjny().getNrRejestracyjny()).orElse(null);
            if (samochod != null && samochod.getWypozyczalnieNazwa() != null) {
                if (!samochod.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa())) {
                    return "redirect:/wypozyczalnia";
                }
            }
        }

        if (result.hasErrors()) {
            return "redirect:/wypozyczalnia?editId=" + (entity.getId() != null ? entity.getId() : "");
        }
        wypozyczalniaRepository.save(entity);
        return "redirect:/wypozyczalnia";
    }

    @GetMapping("/wypozyczalnia/delete/{id}")
    public String delete(@PathVariable Integer id) {
        Wypozyczalnie userWypozyczalnia = getUserWypozyczalnia();

        if (userWypozyczalnia != null) {
            Wypozyczalnia wyp = wypozyczalniaRepository.findById(id).orElse(null);
            if (wyp != null && wyp.getSamochodyNrRejestracyjny() != null) {
                Samochody samochod = wyp.getSamochodyNrRejestracyjny();
                if (samochod.getWypozyczalnieNazwa() != null &&
                        !samochod.getWypozyczalnieNazwa().getNazwa().equals(userWypozyczalnia.getNazwa())) {
                    return "redirect:/wypozyczalnia";
                }
            }
        }

        wypozyczalniaRepository.deleteById(id);
        return "redirect:/wypozyczalnia";
    }
}