package com.qrmatik.server.converter;

import com.qrmatik.server.model.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        return OrderStatus.fromString(dbData);
    }
}
