package com.github.deberth.todo.core.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

public class TodoAuthenticator implements Authenticator<BasicCredentials, User> {

		@Override
		public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
			if ("todosecret".equals(credentials.getPassword())) {
				return Optional.of(new User(credentials.getUsername()));
			}
			return Optional.empty();
		}
}
