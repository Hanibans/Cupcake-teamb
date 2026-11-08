package services;

import entities.Order;
import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import persistence.ConnectionPool;
import persistence.OrderMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderService {
    private ConnectionPool connectionPool;
    private OrderMapper orderMapper;

    public OrderService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.orderMapper = new OrderMapper(connectionPool);
    }

    public void processOrder() throws DatabaseException{
        /*
        get session cart/order
        get total

        get user account balance
        if (userAccountBalance >= orderTotal) {
            gør orderdata klar for service/database
            send orderdata til service/database
            update user account balance i databasen
            bekræft at saldo er opdateret
            redirect til order confirmation
        }
        else {
            send tilbage til forsiden(skiftes ud til cart senere) med besked om at saldo er for lav?
        }
         */
    }

    public void addOrder(User user, List<Orderline> orderlines) throws DatabaseException {
        Order order = new Order(LocalDateTime.now(), 0, "Pending");
        int orderId = orderMapper.addOrder(user, order);
        order.setOrderId(orderId);

        for (Orderline orderline : orderlines) {
            orderMapper.addOrderline(order, orderline);
            order.addOrderline(orderline);
        }

        user.addOrders(order);
    }

    public void addOrderline(List<Orderline> orderlines, Orderline orderline) {
        orderlines.add(orderline);
    }

    public void removeOrderline(List<Orderline> orderlines, Orderline orderline) {
        orderlines.remove(orderline);
    }
}
