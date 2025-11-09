package com.qrmatik.server.converter;

import com.qrmatik.server.dto.MenuItemDto;
import com.qrmatik.server.model.MenuItemEntity;
import org.springframework.stereotype.Component;

@Component
public class MenuItemConverter {

    public MenuItemDto toDto(MenuItemEntity e) {
        if (e == null)
            return null;
        MenuItemDto d = new MenuItemDto();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setPrice(e.getPrice());
        d.setCategory(e.getCategory());
        d.setSubcategory(e.getSubcategory());
        d.setImage(normalizeImage(e.getImage()));
        d.setStockEnabled(e.getStockEnabled());
        d.setStockQuantity(e.getStockQuantity());
        return d;
    }

    private String normalizeImage(String img) {
        if (img == null || img.isBlank())
            return null;
        String s = img.trim();
        // If already absolute URL, leave as is
        String lower = s.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://"))
            return s;
        // Ensure file handler path is absolute so frontend fetches from backend via
        // proxy
        if (s.startsWith("/files/"))
            return s;
        if (s.startsWith("files/"))
            return "/" + s; // make it absolute

        // Legacy stored paths: map to /files handler
        if (s.startsWith("/uploads/")) {
            // strip leading /uploads and prefix with /files
            String rest = s.substring("/uploads/".length());
            return "/files/" + rest;
        }
        if (s.startsWith("uploads/")) {
            String rest = s.substring("uploads/".length());
            return "/files/" + rest;
        }
        if (s.startsWith("/tenants/")) {
            return "/files" + s; // becomes /files/tenants/...
        }
        if (s.startsWith("tenants/")) {
            return "/files/" + s;
        }

        return s;
    }
}
