package com.qrmatik.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulePlanChangeRequest {
  @NotBlank private String plan; // target plan
  @NotBlank private String billingPeriod; // MONTHLY | YEARLY
}
