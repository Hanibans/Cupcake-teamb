package controllers;

import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import persistence.ConnectionPool;
import services.OrderService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private ConnectionPool connectionPool;
    private OrderService orderService;

    public OrderController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.orderService = new OrderService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.post("/orderline/add", ctx -> addOrderline(ctx));
        config.routes.post("/orderline/remove", ctx -> removeOrderline(ctx));
        config.routes.post("/order/checkout", ctx -> checkout(ctx));
    }

    private void addOrderline(Context ctx) {
        int baseId = Integer.parseInt(ctx.formParam("baseId"));
        int toppingId = Integer.parseInt(ctx.formParam("toppingId"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        List<Orderline> orderlines = ctx.sessionAttribute("orderlines");

        if (orderlines == null) {
            orderlines = new ArrayList<>();
        }
    }

        private void removeOrderline (Context ctx){
            int index = Integer.parseInt(ctx.formParam("index"));

            List<Orderline> orderlines = ctx.sessionAttribute("orderlines");

            if (orderlines != null && index >= 0 && index < orderlines.size()) {
                orderlines.remove(index);
            }

            ctx.sessionAttribute("orderlines", orderlines);
            ctx.redirect("/shoppingcart.html");
        }

        private void checkout (Context ctx){
            User user = ctx.sessionAttribute("user");
            List<Orderline> orderlines = ctx.sessionAttribute("orderlines");

            if (user == null) {
                ctx.redirect("/login.html");
                return;
            }

            if (orderlines == null || orderlines.isEmpty()) {
                ctx.redirect("/shoppingcart.html");
                return;
            }

            try {
                orderService.addOrder(user, orderlines);

                // Tøm indkøbskurven
                ctx.sessionAttribute("orderlines", new ArrayList<Orderline>());

                //ctx.redirect("/....html"); - send brugeren til ordrebekræftelsen eller bare en lille tekst??

            } catch (DatabaseException e) {
                ctx.status(500);
                ctx.result("Der opstod en fejl ved oprettelse af ordren.");
            }
        }
    }