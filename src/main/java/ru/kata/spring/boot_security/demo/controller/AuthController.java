package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.validation.Valid; // Импорт для валидации сущностей
import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserServiceImpl userService, RoleServiceImpl roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    // Отображение страницы со списком всех пользователей
    @GetMapping("/admin")
    public String allUsersPage(Model model) {
        model.addAttribute("users", userService.getAllUsers()); // Добавление всех пользователей в модель
        return "users"; // Возврат имени шаблона для отображения
    }

    // Отображение информации о пользователе по его ID
    @GetMapping("/admin/show")
    public String userById(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id)); // Добавление пользователя в модель
        return "user-info"; // Возврат имени шаблона для отображения
    }

    // Показ формы для добавления нового пользователя
    @GetMapping("/admin/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User()); // Добавление нового пустого пользователя в модель
        model.addAttribute("roles", roleService.getListOfRoles()); // Добавление списка ролей в модель
        roleService.getListOfRoles();
        return "add-user"; // Возврат имени шаблона для отображения формы
    }

    // Обработка формы для добавления нового пользователя
    @PostMapping("/admin/addUser")
    public String addUser(@ModelAttribute("user") @Valid User user, // Валидация входных данных
                          @RequestParam(value = "roles", required = false) Long[] roleIds, // Получаем выбранные роли
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) { // Проверка на ошибки валидации
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.getListOfRoles());
            return "add-user";
        }


        // Назначение ролей пользователю
        if (roleIds != null) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : roleIds) {
                Role role = roleService.findRole(roleId); // Получаем роль по ID
                roles.add(role);
            }
            user.setRoles(roles);
        }

        userService.addUser(user); // Сохранение нового пользователя
        return "redirect:/admin";
    }

    // Показ формы для редактирования пользователя по его ID
    @GetMapping("/admin/edit")
    public String editUserById(@RequestParam(value = "id") Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id)); // Добавление редактируемого пользователя в модель
        model.addAttribute("roles", roleService.getListOfRoles()); // Добавление списка ролей в модель
        return "edit-user"; // Возврат имени шаблона для отображения формы
    }

    // Обработка формы для сохранения изменений пользователя
    @PostMapping("/admin/save")
    public String saveUser(@ModelAttribute("user") @Valid User user, // Валидация входных данных
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) { // Проверка на ошибки валидации
            model.addAttribute("user", user); // Возвращаем пользователя с ошибками
            model.addAttribute("roles", roleService.getListOfRoles()); // Возвращаем список ролей
            return "edit-user"; // Повторный показ формы для ввода
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Шифрование пароля пользователя
        userService.editUserById(user); // Сохранение изменений пользователя
        return "redirect:/admin"; // Перенаправление на страницу со списком пользователей
    }

    // Удаление пользователя по его ID
    @GetMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.removeUserById(id); // Удаление пользователя из базы данных
        return "redirect:/admin"; // Перенаправление на страницу со списком пользователей
    }
}
