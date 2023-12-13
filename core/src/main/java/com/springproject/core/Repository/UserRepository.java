package com.springproject.core.Repository;

import com.springproject.core.model.Entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> getByLogin(String Login);
}
