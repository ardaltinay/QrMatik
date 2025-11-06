package com.qrmatik.server.exception;

public class PlanLimitExceededException extends RuntimeException {
  public PlanLimitExceededException(String message) {
    super(message);
  }
}
