package com.westee.cake.service;

import com.westee.cake.entity.TelAndCode;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class CheckTelService {
    private static Pattern pattern =  Pattern.compile("1\\d{10}");
    public Boolean verifyTelParams(TelAndCode telAndCode) {
        if (telAndCode == null) {
            return false;
        }
        if (telAndCode.getTel() == null){
            return false;
        } else {
            return pattern.matcher(telAndCode.getTel()).find();
        }

    }
}
