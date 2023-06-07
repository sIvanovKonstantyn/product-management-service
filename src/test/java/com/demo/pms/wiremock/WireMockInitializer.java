package com.demo.pms.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

/**
 * @author Ivanov Kostiantyn
 */
public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        var wireMockServer = new WireMockServer(
                new WireMockConfiguration().dynamicPort()
                        .stubRequestLoggingDisabled(false)
        );
        wireMockServer.start();
        configurableApplicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

        TestPropertyValues.of(
                "app.clients.exchange-rates.url=" +  wireMockServer.baseUrl()
        ).applyTo(configurableApplicationContext.getEnvironment());

        configurableApplicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });
    }
}
