package com.westee.cake.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeCheckService {
    private final Map<String, String> telToCode = new ConcurrentHashMap<>();

    public void addCode(String tel, String code) {
        telToCode.put(tel, code);
    }

    public String getCorrectCode(String tel) {
        return telToCode.get(tel);
    }
}
