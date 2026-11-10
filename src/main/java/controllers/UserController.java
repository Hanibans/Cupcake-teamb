package controllers;

import entities.User;
import exceptions.DatabaseException;
import exceptions.IllegalUserDataException;
import persistence.*;
import services.*;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.ConnectionPool;
import persistence.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {
    private final ConnectionPool connectionPool;
    private final UserService userService;
    private final CupcakeMapper cupcakeMapper;
    private final UserMapper userMapper;

    public UserController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userService = new UserService(connectionPool);
        this.cupcakeMapper = new CupcakeMapper(connectionPool);
        this.userMapper = new UserMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
            // pages
            config.routes.get("/", ctx -> renderFrontpage(ctx));
            config.routes.get("/frontpage.html", ctx -> renderFrontpage(ctx));
            config.routes.get("/login.html", ctx -> ctx.render("login.html"));
            config.routes.get("/register.html", ctx -> ctx.render("register.html"));
            config.routes.get("/cupcakes.html", ctx -> ctx.render("cupcakes.html"));

            config.routes.get("/payment.html", ctx -> ctx.render("payment.html"));
            config.routes.get("/profile.html", ctx -> renderProfile(ctx));
            config.routes.get("/admin.html", ctx -> {
                if (!requireAdmin(ctx)) return;
                ctx.redirect("/admin"); });

        // form handling
        config.routes.post("/createuser", ctx -> createUser(ctx));
        config.routes.post("/login", ctx -> login(ctx));

            // redirects
            config.routes.get("/createuser", ctx -> ctx.redirect("/register.html"));
            config.routes.get("/login", ctx -> ctx.redirect("/login.html"));
            config.routes.get("/logout", ctx -> logout(ctx));

        config.routes.get("/login/checkout", ctx -> ctx.render("logincheckout"));
        config.routes.post("/login/checkout", ctx -> loginCheckout(ctx));
        config.routes.get("/createuser/checkout", ctx -> ctx.render("createusercheckout"));
        config.routes.post("/createuser/checkout", ctx -> createuserCheckout(ctx));

    }

    public void createUser(Context ctx) {
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

    private boolean requireAdmin(Context ctx) {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.redirect("/login.html");
            return false;
        }
        if (!user.isAdmin()) {
            ctx.redirect("/frontpage.html");
            return false;
        }
        return true;
    }

    public void logout(Context ctx){
        ctx.sessionAttribute("user", null);
        ctx.redirect("/frontpage.html");
    }

    public void loginCheckout(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        User user = userService.login(email, password);
        if (user != null) {
            ctx.sessionAttribute("user", user);
            ctx.redirect("/shoppingcart.html");
        } else {
            ctx.status(400);
            ctx.result("Forkert email eller adgangskode");
        }
    }

    public void createuserCheckout(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        String phoneNumber = ctx.formParam("phonenumber");
        String password = ctx.formParam("password");

        try {
            User user = userService.createUser(name, email, phoneNumber, password);
            ctx.sessionAttribute("user", user);
            ctx.redirect("/shoppingcart.html");
        } catch (IllegalUserDataException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        }
    }


    public void renderFrontpage(Context ctx) throws DatabaseException {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("user", ctx.sessionAttribute("user"));
            model.put("bases", cupcakeMapper.getAllBases());
            model.put("toppings", cupcakeMapper.getAllToppings());
            ctx.render("frontpage.html", model);
        } catch (DatabaseException e) {
            ctx.status(400);
            ctx.result("Kunne ikke finde cupcakes. "+ e.getMessage());
        }
    }

    public void renderProfile(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login.html");
            return;
        }

        ctx.attribute("user", user);
        ctx.attribute("orders", user.getOrders());

        ctx.render("profile.html");
    }

}
