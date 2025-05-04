package com.wiredi.web.sun;

import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.WiredApplication;
import com.wiredi.runtime.WiredApplicationInstance;
import com.wiredi.runtime.async.state.State;
import com.wiredi.runtime.time.Timed;
import com.wiredi.web.UriBuilder;
import com.wiredi.web.WebProperties;
import com.wiredi.web.WebServerProcess;
import com.wiredi.web.WebserverInstance;
import com.wiredi.web.client.RestClient;
import com.wiredi.web.sun.handler.GetRequestHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComSunHttpWebServerIntegrationTest {

    private WiredApplicationInstance application;
    private WireRepository wireRepository;
    private RestClient restClient;

    @BeforeEach
    public void setup() {
        Timed.of(() -> {
            application = WiredApplication.start();
            wireRepository = application.wireRepository();
            restClient = wireRepository.get(RestClient.class);
        }).then(time -> LoggerFactory.getLogger(getClass()).info("Setup done in {}", time));
    }

    @AfterEach
    public void shutdown() {
        application.shutdown();
    }

    @Test
    public void theServerIsStartedWithTheWireRepository() {
        // Arrange Act Assert
        WebServerProcess process = wireRepository.get(WebServerProcess.class);
        State<WebserverInstance> state = process.getState();
        assertTrue(state.isSet());
        assertThat(state.get()).isInstanceOf(SunWebserverInstance.class);
    }

    @Test
    public void theGetRequestHandlerIsInvoked() {
        GetRequestHandler getRequestHandler = wireRepository.get(GetRequestHandler.class);
        WebProperties httpProperties = wireRepository.get(WebProperties.class);

        for (int i = 0; i < 10; i++) {
            Timed.of(() -> {
                // Arrange
                getRequestHandler.getInvocations().prime(1);

                // Act
                TestObject response = restClient.get(
                                UriBuilder.of("http://localhost")
                                        .port(httpProperties.port())
                                        .path(getRequestHandler.endpoint().pattern())
                                        .pathVariable("groupId", "12345")
                                        .pathVariable("userId", "12345")
                        )
                        .withBody(new TestObject("test"))
                        .exchange()
                        .body(TestObject.class);

                // Assert
                assertThat(getRequestHandler.getInvocations().get()).hasSize(1);
                assertThat(response.value()).isEqualTo("User 12345 of group 12345 - Response: test");
            }).then(it -> System.out.println("Test scenario took " + it));
        }
    }
}
