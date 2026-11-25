package com.endava.insuranceapp.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientUpdateRequest(
        @NotBlank(message = "name is required")
        @Size(max = 200, message = "name must be max 200 chars")
        String name,

        @Email(message = "email must be valid")
        @Size(max = 200, message = "email must be max 200 chars")
        String email,

        @Size(max = 50, message = "phone must be max 50 chars")
        String phone,

        @Size(max = 300, message = "address must be max 300 chars")
        String address,

        @Size(max = 255, message = "primaryAddress must be max 255 chars")
        String primaryAddress
) {}
