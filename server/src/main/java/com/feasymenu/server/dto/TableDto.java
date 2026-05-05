package com.feasymenu.server.dto;

import com.feasymenu.server.model.TableEntity;
import com.feasymenu.server.model.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableDto {
    private UUID id;
    private String code;
    private String description;
    private Integer capacity;
    private TableStatus status;

    public static TableDto fromEntity(TableEntity e) {
        if (e == null)
            return null;
        return TableDto.builder().id(e.getId()).code(e.getCode()).description(e.getDescription())
                .capacity(e.getCapacity()).status(e.getStatus()).build();
    }
}
