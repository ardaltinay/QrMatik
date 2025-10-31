package com.qrmatik.server.dto;

import com.qrmatik.server.model.TableStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableUpsertRequest {
    private String code;
    private String description;
    private TableStatus status;
}
