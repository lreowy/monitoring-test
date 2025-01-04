package com.cakey_practice.cakey.controller;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public void createTest(TestDto testDto) {
        Test test = new Test(testDto.text());
    }
}
