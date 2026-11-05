package controllers;

import entities.User;
import exceptions.IllegalUserDataException;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.ConnectionPool;
import persistence.UserMapper;
import services.UserService;

public class UserController {
    private ConnectionPool connectionPool;
    private UserService userService;
    private UserMapper userMapper;

    public UserController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userService = new userService(connectionPool);
        this.userMapper = new userMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.post("/createuser", ctx -> createUser(ctx));
    }

    private void createUser(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        String phoneNumber = ctx.formParam("phonenumber");
        String password = ctx.formParam("password");

        try {
            User user = userService.createUser(name, email, phoneNumber, password);
            ctx.sessionAttribute("user", user);
            ctx.redirect("");
        } catch (IllegalUserDataException e) {
            ctx.status(404);
            ctx.result(e.getMessage());
        }

    }


}
