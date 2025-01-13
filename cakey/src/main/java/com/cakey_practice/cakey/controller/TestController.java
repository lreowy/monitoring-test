package com.cakey_practice.cakey.controller;

import com.cakey_practice.cakey.dto.TestDto;
import com.cakey_practice.cakey.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping("/api/test")
    public ResponseEntity<String> test(TestDto testDto) {
        testService.createTest(testDto);
        return ResponseEntity.ok("Hello World");
    }
}
