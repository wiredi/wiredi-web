package com.wiredi.web.security;

import com.wiredi.web.security.registration.WebSecurityRegistration;

public interface WebSecurityConfiguration {

    void configure(WebSecurityRegistration webSecurity);

}
