package com.company.demo.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.UuidTypeFactory;

import java.util.UUID;

@Source(type = SourceType.APP)
public interface SocialRegistrationConfig extends Config {

    @Default("0fa2b1a5-1d68-4d69-9fbd-dff348347f93")
    @Property("social.defaultGroupId")
    @Factory(factory = UuidTypeFactory.class)
    UUID getDefaultGroupId();
}