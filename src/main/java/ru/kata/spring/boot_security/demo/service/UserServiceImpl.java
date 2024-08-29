package ru.kata.spring.boot_security.demo.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void addUser(User user) {
        encodePassword(user);
        userRepository.save(user);
    }

    private void encodePassword(User user) {
        if (user.getPassword() != null) { // Проверяем, что пароль не пуст
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Кодируем пароль
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        // Инициализируем роли
        Hibernate.initialize(user.getRoles());
        return user;
    }

    @Transactional(readOnly = true)
    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public void editUserById(User user) {
        userRepository.save(user);
    }

    public void removeUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
