package com.feasymenu.server.dto;

import com.feasymenu.server.model.TableStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableInsertRequest {
    private String code;
    private String description;
    private Integer capacity;
    private TableStatus status;
}
