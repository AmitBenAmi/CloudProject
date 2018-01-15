package com.cloudproject.users.controllers;

import static spark.Spark.*;

import java.util.function.Predicate;

public class Router {
	public void get(String path, Predicate<Integer> handle) {
		get(String.format("/%s", path), handle);
	}
}
