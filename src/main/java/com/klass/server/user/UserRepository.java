package com.klass.server.user;

import com.klass.server.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findAllByRole(String role);
    User findByEmail(String email);
}
