package controllers;

import entities.CupcakeBase;
import entities.Order;
import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import persistence.ConnectionPool;
import persistence.CupcakeMapper;
import services.OrderService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private final ConnectionPool connectionPool;
    private final OrderService orderService;
    private final CupcakeMapper cupcakeMapper;

    public OrderController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.orderService = new OrderService(connectionPool);
        this.cupcakeMapper = new CupcakeMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.post("/orderline/add", ctx -> addOrderline(ctx));
        config.routes.post("/orderline/remove", ctx -> removeOrderline(ctx));
        config.routes.post("/order/checkout", ctx -> checkout(ctx));

        // Ny route til at teste betalingslogik
        config.routes.get("/cart/order", ctx -> checkout(ctx));
    }

    private void addOrderline(Context ctx) throws DatabaseException {
        int baseId = Integer.parseInt(ctx.formParam("baseId"));
        int toppingId = Integer.parseInt(ctx.formParam("toppingId"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        // Opret ny List for orderlines, hvis den eksisterer har vi orderlines, hvis den ikke eksisterer
        //  så har vi en null værdi
        if (ctx.sessionAttribute("orderlines") == null){
            List<Orderline> orderlines = new ArrayList<Orderline>();
            ctx.sessionAttribute("orderlines", orderlines);
        }
        List<Orderline> orderlines = ctx.sessionAttribute("orderlines");

        orderlines.add(new Orderline(
                cupcakeMapper.getBaseById(baseId),
                cupcakeMapper.getToppingById(toppingId),
                quantity));

        ctx.redirect("/frontpage.html");
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

    private void checkout (Context ctx) throws DatabaseException {
        // Tjek om bruger er logget ind
        User user = ctx.sessionAttribute("user");

        //Load OrderLines, lige nu er det direkte fra forsiden men senere skal de hentes fra cart
        List<Orderline> orderlines = ctx.sessionAttribute("orderlines");

        // Hvis bruger ikke er logget så redirect til side der redirecter tilbage her efter login
        if (user == null) {
            ctx.redirect("/login/checkout");
            return;
        }

        if (orderlines == null || orderlines.isEmpty()) {
            ctx.redirect("/shoppingcart.html");
            return;
        }

        try {
            if (orderService.addOrder(user, orderlines) == 1){
                // Tøm indkøbskurven
                ctx.sessionAttribute("orderlines", new ArrayList<Orderline>());

                //ctx.redirect("/....html"); - send brugeren til ordrebekræftelsen eller bare en lille tekst??
                ctx.result("Tak for bestillingen!");
            }
            else {
                ctx.result("Ikke nok penge på saldoen. Tilføj et beløb og prøv igen");
                ctx.redirect("/frontpage.html");
            }


        } catch (DatabaseException e) {
            ctx.status(500);
            ctx.result("Der opstod en fejl ved oprettelse af ordren.");
        }
    }
}