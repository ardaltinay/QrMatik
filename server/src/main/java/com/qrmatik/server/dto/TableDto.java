package com.qrmatik.server.dto;

import com.qrmatik.server.model.TableEntity;
import com.qrmatik.server.model.TableStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableDto {
    private UUID id;
    private String code;
    private String description;
    private TableStatus status;

    public static TableDto fromEntity(TableEntity e) {
        if (e == null) return null;
        return TableDto.builder()
                .id(e.getId())
                .code(e.getCode())
                .description(e.getDescription())
                .status(e.getStatus())
                .build();
    }
}
