package com.github.deberth.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TodoConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory;

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Valid
    @NotNull
    @JsonProperty("storage")
    private String storage;

    public DataSourceFactory getSourceFactory() {
        return dataSourceFactory;
    }

    public SwaggerBundleConfiguration getSwaggerConfiguration() {
        return swaggerBundleConfiguration;
    }

    public String getStorage() {
        return storage;
    }
}
