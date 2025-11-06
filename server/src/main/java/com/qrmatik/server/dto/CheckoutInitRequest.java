package com.qrmatik.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutInitRequest {
  @NotBlank private String plan; // STANDARD | PRO
  @NotBlank private String billingPeriod; // MONTHLY | YEARLY
}
