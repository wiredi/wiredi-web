package com.wiredi.web.security.registration;

import com.wiredi.runtime.security.authentication.authorities.Authority;

import java.util.ArrayList;
import java.util.List;

public class EndpointSecurityBuilder {

    private final List<Authority> requiredAuthorities = new ArrayList<>();
    private Boolean mustBeAuthenticated = null;

    public EndpointSecurityBuilder requiresAuthority(Authority authority) {
        requiredAuthorities.add(authority);
        return this;
    }

    public EndpointSecurityBuilder permitAll() {
        mustBeAuthenticated = false;
        return this;
    }

    public EndpointSecurityBuilder mustBeAuthenticated() {
        mustBeAuthenticated = true;
        return this;
    }

    public EndpointSecurity build() {
        if (mustBeAuthenticated == null) {
            throw new IllegalStateException("You must specify either permitAll() or mustBeAuthenticated()");
        }
        return new EndpointSecurity(requiredAuthorities, mustBeAuthenticated);
    }
}
