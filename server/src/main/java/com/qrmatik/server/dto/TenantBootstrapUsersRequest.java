package com.qrmatik.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TenantBootstrapUsersRequest {
  private String adminUsername;
  private String adminPassword;
  private String kitchenUsername;
  private String kitchenPassword;
  private String barUsername;
  private String barPassword;
}
