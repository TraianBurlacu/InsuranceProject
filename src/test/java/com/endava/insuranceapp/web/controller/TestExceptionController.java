package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/error")
public class TestExceptionController {

    @GetMapping("/notfound")
    public void notFound() {
        throw new ResourceNotFoundException("test resource missing");
    }

    @GetMapping("/badrequest")
    public void badRequest() {
        throw new IllegalArgumentException("bad argument");
    }

    @GetMapping("/generic")
    public void generic() {
        throw new RuntimeException("boom");
    }

    public static record TestDto(@NotBlank(message = "name required") String name) {}

    @PostMapping("/validation")
    public void validation(@Valid @RequestBody TestDto dto) {
        // if valid -> 200 OK, if invalid -> MethodArgumentNotValidException
    }
}
