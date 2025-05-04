package com.wiredi.web.security.registration;

import com.wiredi.annotations.Wire;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.integration.EndpointRegistry;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.security.WebSecurityConfiguration;

import java.util.List;
import java.util.function.Function;

@Wire(proxy = false)
public class WebSecurityRegistration {

    private final EndpointRegistry<EndpointSecurity> securityProviderRegistry = new EndpointRegistry<>();

    public WebSecurityRegistration(
            List<WebSecurityConfiguration> configurations
    ) {
        configurations.forEach(configuration -> configuration.configure(this));
    }


    public boolean shouldBeAuthenticated(HttpRequestMessageDetails details) {
        return securityProviderRegistry.get(details.httpEndpoint()) != null;
    }

    public EndpointSecurity getSecurity(HttpRequestMessageDetails details) {
        return securityProviderRegistry.get(details.httpEndpoint());
    }

    public void endpoint(HttpEndpoint endpoint, Function<EndpointSecurityBuilder, EndpointSecurity> builder) {
        EndpointSecurity existingSecurity = securityProviderRegistry.get(endpoint);

        if (existingSecurity != null) {
            throw new IllegalStateException("Endpoint security already registered for " + endpoint);
        }

        EndpointSecurityBuilder endpointSecurityBuilder = new EndpointSecurityBuilder();
        EndpointSecurity security = builder.apply(endpointSecurityBuilder);
        securityProviderRegistry.put(endpoint, security);
    }
}
