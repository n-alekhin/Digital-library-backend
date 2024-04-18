package com.springproject.core.Services;

import com.springproject.core.model.Entity.User;
import com.springproject.core.model.dto.domain.Role;

import java.util.Set;

public interface SecurityService {
    User isAdmin(Long userId);
    User isSuperAdmin(Long userId);
    User isUserNoBan(Long userId);
}
