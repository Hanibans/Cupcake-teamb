package controllers;
import entities.User;
import io.javalin.config.ContextResolverConfig;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.ConnectionPool;

public class CartController {
    ConnectionPool connectionPool;

    public CartController(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.post("/cart/", ctx -> displayCart(ctx));
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
}
