package com.wiredi.web;

import com.wiredi.annotations.Wire;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.async.AsyncLoader;
import com.wiredi.runtime.async.StateFull;
import com.wiredi.runtime.async.state.ModifiableState;
import com.wiredi.runtime.async.state.State;
import com.wiredi.runtime.domain.Eager;
import com.wiredi.runtime.time.Timed;
import com.wiredi.web.exceptions.MissingWebServerException;
import com.wiredi.web.request.HttpRequestChain;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Wire
public class WebServerProcess implements Eager, StateFull<WebserverInstance> {

    private static final Logging logger = Logging.getInstance(WebServerProcess.class);
    private final ModifiableState<WebserverInstance> webserverInstance = State.empty();
    private final WebProperties webProperties;
    private final HttpRequestChain requestChain;

    public WebServerProcess(
            WebProperties webProperties,
            HttpRequestChain requestChain
    ) {
        this.webProperties = webProperties;
        this.requestChain = requestChain;
    }

    @Override
    public @NotNull State<WebserverInstance> getState() {
        return webserverInstance;
    }

    @Override
    public void setup(WireRepository wireRepository) {
        logger.info(() -> "Initializing WebContext");
        AsyncLoader.run(() -> {
            try {
                Webserver webserver = wireRepository.tryGet(Webserver.class)
                        .orElseThrow(() -> new MissingWebServerException("No WebServer found in WireRepository"));

                Timed.of(() -> webserver.start(new WebserverProperties(webProperties.socketAddress(), webProperties.contextPath()), requestChain))
                        .then(timed -> {
                            webserverInstance.set(timed.value());
                            logger.debug(() -> "WebContext on port " + webProperties.port() + " started in " + timed.time());
                        });
            } catch (Throwable e) {
                webserverInstance.markAsDirty(e);
            }
        });
    }

    @Override
    public void dismantleState() {
        webserverInstance.ifPresent(webServer -> {
            try {
                Timed.of(webServer::stop)
                        .then(timed -> logger.debug(() -> "WebContext on port " + webProperties.port() + " stopped in " + timed));
            } catch (IOException e) {
                webserverInstance.markAsDirty(e);
            }
        });
    }
}
