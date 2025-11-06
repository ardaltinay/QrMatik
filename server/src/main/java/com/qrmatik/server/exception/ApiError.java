package com.qrmatik.server.exception;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(OffsetDateTime timestamp, int status, String error, String message, String path, String code,
        List<String> details) {
}
