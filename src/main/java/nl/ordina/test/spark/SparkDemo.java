package nl.ordina.test.spark;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.exception;
import static spark.Spark.get;

public class SparkDemo {
    public static void main(String[] args)
    {

        //simple example
        get("/hello", (request, response) -> "Hello World!");

        //example with JSON parsing
        Gson gson = new Gson();
        get("/user", (request, response) -> getAllUsers(), gson::toJson);

        //exception handling
        exception(RuntimeException.class, (e, request, response) -> {response.status(400); response.body("bad request");});
        get("/exception", (request, response) -> {throw new RuntimeException();});
    }

    private static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setName("Pepe");
        users.add(user);
        return users;
    }

}
