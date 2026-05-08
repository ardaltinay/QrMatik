package com.feasymenu.server.converter;

import com.feasymenu.server.dto.OrderDto;
import com.feasymenu.server.dto.OrderLineDto;
import com.feasymenu.server.model.OrderEntity;
import com.feasymenu.server.model.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OrderConverter {

    public OrderDto toDto(OrderEntity e) {
        List<OrderLineDto> lines = new ArrayList<>();
        if (e.getLines() != null) {
            for (OrderItemEntity li : e.getLines()) {
                UUID itemId = (li.getMenuItem() != null ? li.getMenuItem().getId() : null);
                OrderLineDto d = new OrderLineDto();
                d.setItemId(itemId);
                d.setName(li.getNameSnapshot());
                d.setPrice(li.getUnitPrice());
                d.setQuantity(li.getQuantity());
                d.setImage(li.getImageSnapshot());
                d.setCategory(li.getCategorySnapshot());
                d.setSubcategory(li.getSubcategorySnapshot());
                d.setNote(li.getNote());
                lines.add(d);
            }
        }
        OrderDto dto = new OrderDto();
        dto.setId(e.getId());
        String code = null;
        if (e.getTable() != null) {
            try {
                code = e.getTable().getCode();
            } catch (Exception ignore) {
            }
        }
        dto.setTableCode(code);
        dto.setSessionId(e.getSessionId());
        try {
            dto.setTenantCode(e.getTenant() != null ? e.getTenant().getCode() : null);
        } catch (Exception ignore) {
            dto.setTenantCode(null);
        }
        dto.setCustomerName(e.getCustomerName());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setKitchenStatus(e.getKitchenStatus() != null ? e.getKitchenStatus().name() : null);
        dto.setBarStatus(e.getBarStatus() != null ? e.getBarStatus().name() : null);
        try {
            if (e.getTotal() != null) {
                dto.setTotal(e.getTotal());
            } else {
                BigDecimal sum = BigDecimal.ZERO;
                for (OrderLineDto d : lines) {
                    if (d.getPrice() != null && d.getQuantity() != null) {
                        sum = sum.add(d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())));
                    }
                }
                dto.setTotal(sum);
            }
        } catch (Exception ignore) {
            dto.setTotal(e.getTotal());
        }
        dto.setCreatedTime(e.getCreatedTime());
        try {
            if (e.getCreatedTime() != null) {
                // createdTime artık Instant (UTC) olarak tutuluyor.
                // İstemciye OffsetDateTime olarak dönmek için doğrudan UTC'ye çeviriyoruz.
                dto.setCreatedAt(e.getCreatedTime().atOffset(java.time.ZoneOffset.UTC));
            }
        } catch (Exception ignore) {
        }
        dto.setSessionExpiresAt(e.getSessionExpiresAt());
        dto.setLines(lines);
        return dto;
    }
}
