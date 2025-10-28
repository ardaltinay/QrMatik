package com.qrmatik.server.converter;

import com.qrmatik.server.dto.MenuItemDto;
import com.qrmatik.server.model.MenuItemEntity;
import org.springframework.stereotype.Component;

@Component
public class MenuItemConverter {

    public MenuItemDto toDto(MenuItemEntity e) {
        if (e == null) return null;
        MenuItemDto d = new MenuItemDto();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setPrice(e.getPrice());
        d.setCategory(e.getCategory());
        d.setSubcategory(e.getSubcategory());
        d.setImage(e.getImage());
        return d;
    }
}
