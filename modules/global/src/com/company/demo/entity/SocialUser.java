package com.company.demo.entity;

import javax.persistence.Entity;
import com.haulmont.cuba.core.entity.annotation.Extends;
import javax.persistence.Column;
import com.haulmont.cuba.security.entity.User;

@Extends(User.class)
@Entity(name = "demo$SocialUser")
public class SocialUser extends User {
    private static final long serialVersionUID = -2823117926539607534L;

    @Column(name = "FACEBOOK_ID")
    protected String facebookId;

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookId() {
        return facebookId;
    }
}