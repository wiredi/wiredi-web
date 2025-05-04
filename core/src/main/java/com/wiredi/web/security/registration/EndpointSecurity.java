package com.wiredi.web.security.registration;

import com.wiredi.runtime.security.authentication.Authentication;
import com.wiredi.runtime.security.authentication.authorities.Authority;
import com.wiredi.runtime.security.exceptions.UnauthenticatedException;
import com.wiredi.runtime.security.exceptions.UnauthorizedException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EndpointSecurity {

    private final List<Authority> requiredAuthorities;
    private final boolean mustBeAuthenticated;

    public EndpointSecurity(List<Authority> requiredAuthorities, boolean mustBeAuthenticated) {
        this.requiredAuthorities = requiredAuthorities;
        this.mustBeAuthenticated = mustBeAuthenticated;
    }

    public void verify(@Nullable Authentication authentication) {
        if (!mustBeAuthenticated) {
            return;
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthenticatedException();
        }

        for (Authority requiredAuthority : requiredAuthorities) {
            for (Authority authority : authentication.authorities()) {
                if (!authority.equals(requiredAuthority)) {
                    throw new UnauthorizedException();
                }
            }
        }
    }
}
