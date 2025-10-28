package com.qrmatik.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpsertRequest {
    private String username;
    private String role;
    private String password; // plain text; will be encoded server-side
}
