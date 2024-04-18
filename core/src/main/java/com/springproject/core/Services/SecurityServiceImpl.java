package com.springproject.core.Services;

import com.springproject.core.Repository.UserRepository;
import com.springproject.core.model.Entity.User;
import com.springproject.core.model.dto.domain.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SecurityServiceImpl implements SecurityService{
    private final UserRepository userRepository;

    public SecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User isAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
        String role = user.getRole();
        if (user.getIsBanned()){
            throw new AccessDeniedException("Пользователь забанен");
        }
        if ( (!Objects.equals(role, Role.ADMIN.name())  && (!Objects.equals(role, Role.SUPER_ADMIN.name())))){
            throw new AccessDeniedException("У Пользователя нет разрешения на выполнение этого действия");
        }
        return null;
    }

    @Override
    public User isSuperAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
        String role = user.getRole();
        if (user.getIsBanned()){
            throw new AccessDeniedException("Пользователь забанен");
        }
        if ( !Objects.equals(role, Role.SUPER_ADMIN.name())){
            throw new AccessDeniedException("У Пользователя нет разрешения на выполнение этого действия");
        }
        return null;
    }

    @Override
    public User isUserNoBan(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
        if (user.getIsBanned()){
            throw new AccessDeniedException("Пользователь забанен");
        }
        return null;
    }
}
