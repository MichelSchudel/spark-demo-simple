package nl.ordina.test.spark;

import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;

public class SparkDemo {

    private static Map<String,User> users = new ConcurrentHashMap<>();

    public static void main(String[] args)
    {
        //simple example
        initializeUsers();

        get("/hello", (request, response) -> "Hello World!");

        //example with JSON parsing
        Gson gson = new Gson();

        get("/user", (request, response) -> getAllUsers(), gson::toJson);

        get("/user/:name", (request, response) -> getUser(request.params("name")), gson::toJson);

        put("/user", (request, response) -> addUser(gson.fromJson(request.body(), User.class)), gson::toJson);

        delete("/user/:name", (request, response) -> removeUser(request.params("name")), gson::toJson);

        //exception handling
        exception(RuntimeException.class, (e, request, response) -> {response.status(400); response.body("bad request");});
        exception(NoSuchElementException.class, (e, request, response) -> {response.status(404); response.body("not found");});
        get("/exception", (request, response) -> {throw new RuntimeException();});
    }

    private static void initializeUsers() {
        User user = new User();
        user.setName("Pepe");
        users.put(user.getName(), user);
    }

    private static User getUser(String name) {
        return Optional.ofNullable(users.get(name)).orElseThrow(() -> new NoSuchElementException());
    }

    private static Collection<User> removeUser(String name) {
        Optional.ofNullable(users.remove(name)).orElseThrow(() -> new NoSuchElementException());
        return getAllUsers();
    }

    private static Collection<User> addUser(User user) {
        users.put(user.getName(),user);
        return getAllUsers();
    }

    private static Collection<User> getAllUsers() {
        return users.values();
    }

}


