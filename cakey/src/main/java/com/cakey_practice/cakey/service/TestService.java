package com.cakey_practice.cakey.service;

import com.cakey_practice.cakey.domain.Test;
import com.cakey_practice.cakey.dto.TestDto;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public void createTest(TestDto testDto) {
        Test test = new Test(testDto.text());
    }
}
