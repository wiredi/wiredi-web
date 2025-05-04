package com.wiredi.web.security;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.security.SecurityContext;
import com.wiredi.runtime.security.authentication.Authentication;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import com.wiredi.web.request.HttpRequestChainContext;
import com.wiredi.web.request.HttpRequestChainElement;
import com.wiredi.web.security.registration.EndpointSecurity;
import com.wiredi.web.security.registration.WebSecurityRegistration;
import org.jetbrains.annotations.Nullable;

@Wire(proxy = false)
public class WebSecurityRequestChainElement implements HttpRequestChainElement {

    private final WebSecurityRegistration securityRegistration;
    private final SecurityContext securityContext;

    public WebSecurityRequestChainElement(
            WebSecurityRegistration securityRegistration,
            SecurityContext securityContext
    ) {
        this.securityRegistration = securityRegistration;
        this.securityContext = securityContext;
    }

    @Override
    public Message<HttpResponseMessageDetails> handle(Message<HttpRequestMessageDetails> request, HttpRequestChainContext chain) {
        @Nullable
        Authentication authentication = securityContext.getAuthenticationOf(request.details());
        securityContext.setAuthentication(authentication);

        try {
            EndpointSecurity security = securityRegistration.getSecurity(request.details());
            if (security != null) {
                security.verify(authentication);
            }

            return chain.next(request);
        } finally {
            securityContext.clearAuthentication();
        }
    }
}
