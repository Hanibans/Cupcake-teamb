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
