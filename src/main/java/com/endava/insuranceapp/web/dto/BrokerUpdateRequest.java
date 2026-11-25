package com.endava.insuranceapp.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BrokerUpdateRequest(
        @NotBlank(message = "name is required")
        @Size(max = 200, message = "name must be max 200 chars")
        String name,

        @Email(message = "email must be valid")
        @Size(max = 200)
        String email,

        @Size(max = 50)
        String phone,

        @NotNull(message = "isActive is required")
        Boolean isActive,

        @DecimalMin(value = "0.0")
        @DecimalMax(value = "1.0")
        BigDecimal commissionPercentage

) {}
