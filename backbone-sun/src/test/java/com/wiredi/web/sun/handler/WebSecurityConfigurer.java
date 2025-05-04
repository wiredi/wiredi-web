package com.wiredi.web.sun.handler;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.runtime.security.authentication.Authentication;
import com.wiredi.runtime.security.authentication.AuthenticationProvider;
import com.wiredi.runtime.security.authentication.GenericAuthenticationProvider;
import com.wiredi.runtime.security.authentication.TokenAuthentication;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.messaging.HttpMessageDetails;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.security.WebSecurityConfiguration;
import com.wiredi.web.security.registration.WebSecurityRegistration;
import org.jetbrains.annotations.Nullable;

@Wire(proxy = false)
public class WebSecurityConfigurer implements WebSecurityConfiguration {
    @Override
    public void configure(WebSecurityRegistration webSecurity) {
        webSecurity.endpoint(new HttpEndpoint("/secure", HttpMethod.GET), endpoint -> endpoint.mustBeAuthenticated()
                .build());
    }
}

@Wire(proxy = false, to = AuthenticationProvider.class)
class TestAuthenticationProvider implements GenericAuthenticationProvider<HttpMessageDetails> {

    @Override
    public @Nullable Authentication extractFrom(HttpMessageDetails details) {
        if (details instanceof HttpRequestMessageDetails requestMessageDetails) {
            MessageHeader authHeader = requestMessageDetails.request().headers().firstValue("Auth");
            if (authHeader != null) {
                TokenAuthentication authentication = new TokenAuthentication(authHeader.decodeToString());
                authentication.setAuthenticated(authHeader.decodeToString().equals("auth"));
                return authentication;
            }
        }

        return null;
    }

    @Override
    public Class<HttpMessageDetails> sourceClass() {
        return HttpMessageDetails.class;
    }
}

