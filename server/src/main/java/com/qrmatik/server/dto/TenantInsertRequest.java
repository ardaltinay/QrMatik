package com.qrmatik.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TenantInsertRequest {
  private String code;
  private String name;
  private String ownerName;
  private String ownerEmail;
  private String logoUrl;
  private String primaryColor;
  private String accentColor;
  private String config; // raw JSON string (optional)
  private String plan; // optional: FREE | STANDARD | PRO (admin-only)
  private String customDomain; // optional: PRO only
}
