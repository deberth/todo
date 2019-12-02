package com.github.deberth.todo.core.auth;

import java.security.Principal;

public class User implements Principal {

		private final String name;

		public User(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
}
