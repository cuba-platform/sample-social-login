package com.company.demo.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.APP)
public interface FacebookConfig extends Config {
    @Default("id,name,email")
    @Property("facebook.fields")
    String getFacebookFields();

    @Property("facebook.appId")
    String getFacebookAppId();

    @Property("facebook.appSecret")
    String getFacebookAppSecret();

    @Property("facebook.scope")
    String getFacebookAppScope();
}