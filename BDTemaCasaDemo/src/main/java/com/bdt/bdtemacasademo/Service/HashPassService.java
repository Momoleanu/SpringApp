package com.bdt.bdtemacasademo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HashPassService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String hashed(String password)
    {
        return passwordEncoder.encode(password);
    }

    public boolean checked(String rawPass, String hashPass)
    {
        return passwordEncoder.matches(rawPass, hashPass);
    }
}
