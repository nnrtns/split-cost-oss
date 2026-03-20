package com.split.expenseSplitter.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerContractWebMvcTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(
                        new GlobalExceptionHandler(),
                        new GlobalValidationExceptionHandler()
                )
                .build();
    }

    @Test
    void shouldMapValidationExceptionTo422() throws Exception {
        mockMvc.perform(get("/test-exceptions/validation"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("validation-problem"));
    }

    @Test
    void shouldMapRuntimeExceptionTo422() throws Exception {
        mockMvc.perform(get("/test-exceptions/runtime"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("boom"));
    }

    @Test
    void shouldMapDuplicateInsertionExceptionTo409() throws Exception {
        mockMvc.perform(get("/test-exceptions/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("duplicate"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test-exceptions/validation")
        ResponseEntity<Void> validation() {
            throw new ValidationException("validation-problem");
        }

        @GetMapping("/test-exceptions/runtime")
        ResponseEntity<Void> runtime() {
            throw new RuntimeException("boom");
        }

        @GetMapping("/test-exceptions/duplicate")
        ResponseEntity<Void> duplicate() {
            throw new DuplicateInsertionException("duplicate");
        }
    }
}