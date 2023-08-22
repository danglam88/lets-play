package com.LetsPlay.repository;

import com.LetsPlay.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findUserByName(String name);
    Optional<User> findUserByEmail(String email);
    List<User> findUserByRole(String role);
}
