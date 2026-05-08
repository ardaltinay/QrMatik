package com.feasymenu.server.dto;

import lombok.Data;

@Data
public class CallWaiterRequest {
    private String tableCode;
    private String sessionId;
}
