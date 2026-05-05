package com.feasymenu.server.converter;

import com.feasymenu.server.dto.UserDto;
import com.feasymenu.server.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserDto toDto(UserEntity e) {
        if (e == null)
            return null;
        UserDto d = new UserDto();
        d.setId(e.getId());
        d.setUsername(e.getUsername());
        d.setRole(e.getRole() != null ? e.getRole().name().toLowerCase() : null);
        return d;
    }
}
