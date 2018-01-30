package com.company.demo.restapi;

import com.company.demo.service.FacebookService;
import com.company.demo.service.FacebookService.FacebookUserData;
import com.company.demo.service.FacebookService.OAuth2ResponseType;
import com.company.demo.service.SocialRegistrationService;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.TrustedClientService;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import com.haulmont.restapi.auth.OAuthTokenIssuer;
import com.haulmont.restapi.auth.OAuthTokenIssuer.OAuth2AccessTokenResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collections;
import java.util.function.Supplier;

@RestController
@RequestMapping("facebook")
public class FacebookAuthenticationController {
    @Inject
    private Configuration configuration;
    @Inject
    private MessageTools messageTools;
    @Inject
    private OAuthTokenIssuer oAuthTokenIssuer;
    @Inject
    private TrustedClientService trustedClientService;
    @Inject
    private FacebookService facebookService;
    @Inject
    private SocialRegistrationService socialRegistrationService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get() {
        String loginUrl = getAsPrivilegedUser(() ->
                facebookService.getLoginUrl(getAppUrl(), OAuth2ResponseType.CODE_TOKEN)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, loginUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // here we check facebook code, obtain access code and issue token using OAuthTokenIssuer
    @RequestMapping(method = RequestMethod.POST, value = "login")
    public ResponseEntity<OAuth2AccessToken> login(@RequestParam("code") String code) {
        User user = getAsPrivilegedUser(() -> {
            FacebookUserData userData = facebookService.getUserData(getAppUrl(), code);

            return socialRegistrationService.findOrRegisterUser(
                    userData.getId(), userData.getEmail(), userData.getName());
        });

        OAuth2AccessTokenResult tokenResult = oAuthTokenIssuer.issueToken(user.getLogin(),
                messageTools.getDefaultLocale(), Collections.emptyMap());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
        headers.set(HttpHeaders.PRAGMA, "no-cache");
        return new ResponseEntity<>(tokenResult.getAccessToken(), headers, HttpStatus.OK);
    }

    private <T> T getAsPrivilegedUser(Supplier<T> supplier) {
        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);
        UserSession session;
        try {
            session = trustedClientService.getSystemSession(webAuthConfig.getTrustedClientPassword());
        } catch (LoginException e) {
            throw new RuntimeException("Unable to use system session", e);
        }

        return AppContext.withSecurityContext(new SecurityContext(session), supplier::get);
    }

    private String getAppUrl() {
        GlobalConfig globalConfig = configuration.getConfig(GlobalConfig.class);
        String webAppUrl = globalConfig.getWebAppUrl();
        if (!webAppUrl.endsWith("/")) {
            webAppUrl += "/";
        }
        return webAppUrl + "VAADIN/facebook-login-demo.html";
    }
}