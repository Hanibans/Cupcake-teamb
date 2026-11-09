package services;

import entities.Order;
import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import persistence.ConnectionPool;
import persistence.OrderMapper;
import persistence.UserMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderService {
    private ConnectionPool connectionPool;
    private OrderMapper orderMapper;
    private UserMapper userMapper;

    public OrderService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.orderMapper = new OrderMapper(connectionPool);
        this.userMapper = new UserMapper(connectionPool);
    }

    //Order Processing
    public Order processOrder(User user, List<Orderline> orderlines) throws DatabaseException{
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
        Order order = new Order(LocalDateTime.now(), 0, "Pending");
        for (Orderline orderline : orderlines) {
            order.addOrderline(orderline);
        }

        int userAccountBalance = user.getAccountBalance();
        int orderPrice = order.getTotalPrice();

        if (userAccountBalance >= order.getTotalPrice()){
            int newBalance = userAccountBalance - orderPrice;
            user.setAccountBalance(newBalance);
            userMapper.updateAccountBalance(user.getUserId(), newBalance);
            return order;
        }
        else {
            return null;
        }
    }

    // returnerer 1 hvis successful og 0 hvis fejlet
    public int addOrder(User user, List<Orderline> orderlines) throws DatabaseException {
        Order order = processOrder(user, orderlines);
        if (order != null){
            int orderId = orderMapper.addOrder(user, order);
            order.setOrderId(orderId);

            for (Orderline orderline : order.getOrderlines()){
                orderMapper.addOrderline(order, orderline);
            }
            user.addOrders(order);
            return 1;
        }
        else {
            return 0;
        }

    }

    public void addOrderline(List<Orderline> orderlines, Orderline orderline) {
        orderlines.add(orderline);
    }

    public void removeOrderline(List<Orderline> orderlines, Orderline orderline) {
        orderlines.remove(orderline);
    }
}
