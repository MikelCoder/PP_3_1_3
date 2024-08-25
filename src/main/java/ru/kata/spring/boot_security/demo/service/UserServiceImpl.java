package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepository = userRepo;
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.getById(id);
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
