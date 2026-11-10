package controllers;
import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import io.javalin.config.ContextResolverConfig;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.ConnectionPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartController {
    ConnectionPool connectionPool;

    public CartController(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.post("/cart", ctx -> displayCart(ctx));
        config.routes.get("/shoppingcart.html", ctx -> renderShoppingcart(ctx));
    }

    public void displayCart(Context ctx){
        User user = ctx.sessionAttribute("user");
        /*
        if (user == null) {
            ctx.redirect("/login");
            return;
        }
         */
        // Ingen cart lige pt. så vi redirecter direkte til order processing
        /*
        ctx.render("cart.html")
                .attribute("cart", user.getCart());
         */

        ctx.redirect("/cart/order");
    }

    public void renderShoppingcart(Context ctx){
        if (ctx.sessionAttribute("orderlines") == null){
            List<Orderline> orderlines = new ArrayList<Orderline>();
            ctx.sessionAttribute("orderlines", orderlines);
        }
        List<Orderline> orderlines = ctx.sessionAttribute("orderlines");

        Map<String, Object> cart = new HashMap<>();
        cart.put("cart", orderlines);

        int total = 0;
        for(Orderline orderline : orderlines){
            total += orderline.getLinePrice();
        }
        cart.put("totalPrice", total);

        ctx.render("shoppingcart.html", cart);

    }
}