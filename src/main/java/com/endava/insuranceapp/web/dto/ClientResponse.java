package com.endava.insuranceapp.web.dto;

public record ClientResponse(
        Long id,
        String type,
        String name,
        String identifier,
        String email,
        String phone,
        String address,
        String primaryAddress
) {}
