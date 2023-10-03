package ru.nsu.fit.digitallibrary.repositories;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.fit.digitallibrary.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);
}
