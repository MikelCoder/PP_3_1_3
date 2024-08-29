package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.Query;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
//    User findUserWithRoles(Long id);


    User findByLogin(String login);
}