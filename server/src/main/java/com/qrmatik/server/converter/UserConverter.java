package com.qrmatik.server.converter;

import com.qrmatik.server.dto.UserDto;
import com.qrmatik.server.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserDto toDto(UserEntity e) {
        if (e == null) return null;
        UserDto d = new UserDto();
        d.setId(e.getId());
        d.setUsername(e.getUsername());
        d.setRole(e.getRole());
        return d;
    }
}
