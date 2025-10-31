package com.qrmatik.server.converter;

import com.qrmatik.server.dto.OrderDto;
import com.qrmatik.server.dto.OrderLineDto;
import com.qrmatik.server.model.OrderEntity;
import com.qrmatik.server.model.OrderItemEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

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
        // Read table code from relation only
        String code = null;
        if (e.getTable() != null) {
            try {
                code = e.getTable().getCode();
            } catch (Exception ignore) {
                /* lazy load issues */ }
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
        dto.setTotal(e.getTotal());
        dto.setCreatedTime(e.getCreatedTime());
        dto.setSessionExpiresAt(e.getSessionExpiresAt());
        dto.setLines(lines);
        return dto;
    }
}
