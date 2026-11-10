package controllers;

import entities.CupcakeBase;
import entities.Order;
import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import persistence.ConnectionPool;
import persistence.CupcakeMapper;
import persistence.OrderMapper;
import persistence.UserMapper;
import services.OrderService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderController {
    private final ConnectionPool connectionPool;
    private final OrderService orderService;
    private final CupcakeMapper cupcakeMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public OrderController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.orderService = new OrderService(connectionPool);
        this.cupcakeMapper = new CupcakeMapper(connectionPool);
        this.userMapper = new UserMapper(connectionPool);
        this.orderMapper = new OrderMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.post("/orderline/add", ctx -> addOrderline(ctx));
        config.routes.post("/orderline/remove", ctx -> removeOrderline(ctx));
        config.routes.post("/order/checkout", ctx -> checkout(ctx));
        config.routes.post("/orderline/reduce", ctx -> reduceOrderAmount(ctx));
        config.routes.post("/orderline/increase", ctx -> increaseOrderAmount(ctx));
        config.routes.get("/admin", ctx -> showAllOrders(ctx));
        config.routes.get("/admin/orders", ctx -> showOrder(ctx));
        config.routes.post("/admin/orders/status", ctx -> updateOrderStatus(ctx));

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


    private void reduceOrderAmount (Context ctx){
        int index = Integer.parseInt(ctx.formParam("index"));
        List<Orderline> orderlines = ctx.sessionAttribute("orderlines");
        if (orderlines != null && index >= 0 && index < orderlines.size()) {
            Orderline orderline = orderlines.get(index);
            int orderlineQuantity = orderline.getQuantity();
            orderline.setQuantity(orderlineQuantity -1);
            if(orderline.getQuantity() == 0){
                orderlines.remove(index);}
        }
        ctx.sessionAttribute("orderlines", orderlines);
        ctx.redirect("/shoppingcart.html");
    }

    private void increaseOrderAmount (Context ctx){
        int index = Integer.parseInt(ctx.formParam("index"));
        List<Orderline> orderlines = ctx.sessionAttribute("orderlines");
        if (orderlines != null && index >= 0 && index < orderlines.size()) {
            Orderline orderline = orderlines.get(index);
            int orderlineQuantity = orderline.getQuantity();
            orderline.setQuantity(orderlineQuantity +1);
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
                // Regn totalen ud imens vi stadig har listen
                int total = 0;
                for (Orderline orderline : orderlines){
                    total += orderline.getLinePrice();
                }
                //Læg data i en Map til bekræftelsessiden
                Map<String, Object> model = new HashMap<>();
                model.put("orderlines", orderlines);
                model.put("totalPrice", total);

                // Tøm indkøbskurven
                ctx.sessionAttribute("orderlines", new ArrayList<Orderline>());

                //ctx.redirect("/....html"); - send brugeren til ordrebekræftelsen eller bare en lille tekst??
                // Udkommenteret: erstattet af en rigtig bekræftelsesside
                // ctx.result("Tak for bestillingen!");

                //Vis bekræftelsessiden med det kunden har bestilt
                ctx.render("orderconfirmation.html", model);
            }
            else {
                ctx.sessionAttribute("errorMessage", "Ikke nok penge på saldoen. Tilføj et beløb og prøv igen");
                ctx.redirect("/shoppingcart.html");
            }


        } catch (DatabaseException e) {
            ctx.status(500);
            ctx.result("Der opstod en fejl ved oprettelse af ordren.");
        }
    }

    private void showAllOrders(Context ctx) throws DatabaseException {
        User user = ctx.sessionAttribute("user");

        if (user == null || !user.isAdmin()) {
            ctx.status(403).result("Adgang nægtet");
            return;
        }

        List<Order> orders = orderService.getAllOrders();

        System.out.println("Antal ordrer hentet: " + orders.size());

        ctx.attribute("orders", orders);
        ctx.attribute("selectedOrder", null);
        ctx.attribute("customers", userMapper.getAllCustomers());

        // US-6 feature: Henter "Ordre gemt"-beskeden fra sessionen
        // og fjerner den, så den kun vises én gang efter redirect
        ctx.attribute("adminMessage", ctx.consumeSessionAttribute("adminMessage"));

        ctx.render("admin.html");
    }

    private void showOrder(Context ctx) throws DatabaseException {
        User user = ctx.sessionAttribute("user");

        if (user == null || !user.isAdmin()) {
            ctx.status(403).result("Adgang nægtet");
            return;
        }

        int orderId = Integer.parseInt(ctx.queryParam("orderId"));

        List<Order> orders = orderService.getAllOrders();

        Integer selectedOrderId = ctx.sessionAttribute("selectedOrderId");

        Order selectedOrder = null;

        if (selectedOrderId == null || selectedOrderId != orderId) {
            selectedOrder = orderService.getOrderById(orderId);
            ctx.sessionAttribute("selectedOrderId", orderId);
        } else {
            ctx.sessionAttribute("selectedOrderId", null);
        }

        ctx.attribute("orders", orders);
        ctx.attribute("selectedOrder", selectedOrder);
        ctx.attribute("customers", userMapper.getAllCustomers());

        // US-6 feature: "Ordre gemt"-besked
        ctx.attribute("adminMessage", ctx.consumeSessionAttribute("adminMessage"));

        ctx.render("admin.html");
    }

    private void updateOrderStatus(Context ctx) throws DatabaseException {
        User user = ctx.sessionAttribute("user");

        if (user == null || !user.isAdmin()) {
            ctx.status(400).result("Adgang nægtet");
            return;
        }

        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        String status = ctx.formParam("status");

        // Kun tilladte statusser
        if (!List.of("Pending", "Completed", "Cancelled").contains(status)) {
            ctx.status(400).result("Ugyldig status.");
            return;
        }

        orderMapper.updateOrderStatus(orderId, status);
        ctx.sessionAttribute("adminMessage", "Ordre #"+ orderId + "gemt");
        ctx.redirect("/admin");
    }

}