package controllers;

import entities.User;
import exceptions.IllegalUserDataException;
import persistence.*;
import services.*;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.ConnectionPool;
import persistence.UserMapper;

public class UserController {
    private ConnectionPool connectionPool;
    private UserService userService;
    private UserMapper userMapper;

    public UserController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userService = new UserService(connectionPool);
        this.userMapper = new UserMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.post("/createuser", ctx -> createUser(ctx));
        config.routes.get("/createuser", ctx -> ctx.redirect("/register.html"));
        config.routes.post("/login", ctx -> login(ctx));
        config.routes.get("/login", ctx -> ctx.redirect("/login.html"));

    }

    private void createUser(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        String phoneNumber = ctx.formParam("phonenumber");
        String password = ctx.formParam("password");

        try {
            User user = userService.createUser(name, email, phoneNumber, password);
            ctx.sessionAttribute("user", user);
            ctx.redirect("/frontpage.html");
        } catch (IllegalUserDataException e) {
            ctx.status(404);
            ctx.result(e.getMessage());
        }

    }

    public void login(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        User user = userService.login(email, password);
        if (user != null) {
            ctx.sessionAttribute("user", user);
            ctx.redirect("/frontpage.html");
        } else {
            ctx.status(400);
            ctx.result("Forkert email eller adgangskode");
        }
    }


}
