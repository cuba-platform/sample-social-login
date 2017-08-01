package com.company.demo.service;

import com.haulmont.cuba.security.entity.User;

public interface SocialRegistrationService {
    String NAME = "demo_SocialRegistrationService";

    User findOrRegisterUser(String facebookId, String email, String name);
}