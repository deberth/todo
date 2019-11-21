package com.github.deberth.todo;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class todoApplication extends Application<todoConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(todoApplication.class);

    public static void main(final String[] args) throws Exception {
        new todoApplication().run(args);
    }

    @Override
    public String getName() {
        return "todo";
    }

    @Override
    public void initialize(final Bootstrap<todoConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final todoConfiguration configuration,
                    final Environment environment) {
        LOGGER.info("Starting application");
    }

}
