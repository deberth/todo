package com.github.deberth.todo.health;

import com.codahale.metrics.health.HealthCheck;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class AppCheck extends HealthCheck {

    private final Client client;

    public AppCheck(Client client) {
        super();
        this.client = client;
    }

    @Override
    protected Result check() throws Exception {

        WebTarget webTarget = client.target("http://localhost:8080/todos");
        Invocation.Builder invocationBuilder =  webTarget.register(HttpAuthenticationFeature.basic("healthcheck", "todosecret")).request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        @SuppressWarnings("rawtypes")
        List todos = response.readEntity(List.class);
        if(todos !=null && response.getStatus() == HttpStatus.OK_200){
            return Result.healthy();
        }
        return Result.unhealthy("App failed");
    }
}