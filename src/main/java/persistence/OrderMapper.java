package persistence;

import entities.*;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class OrderMapper {
    ConnectionPool connectionPool;
    private static final Logger logger = LoggerFactory.getLogger(OrderMapper.class);

    public OrderMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int addOrder(User user, Order order) throws DatabaseException {

        String sql = """
            INSERT INTO orders
            (user_id, order_date, total_price, order_status)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, user.getUserId());
            ps.setTimestamp(2, Timestamp.valueOf(order.getDate()));
            ps.setInt(3, order.getTotalPrice());
            ps.setString(4, order.getOrderStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new DatabaseException("Kunne ikke oprette en ordre.");

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void addOrderline(Order order, Orderline orderline)
            throws DatabaseException {

        String sql = """
            INSERT INTO orderline
            (order_id, base_id, topping_id, quantity)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, order.getOrderId());
            ps.setInt(2, orderline.getCupcakeBase().getCupcakeId());
            ps.setInt(3, orderline.getCupcakeTopping().getCupcakeId());
            ps.setInt(4, orderline.getQuantity());

            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Kunne ikke oprette en ordrelinje.", e);
            throw new DatabaseException(e.getMessage());
        }
    }

    public List<Order> getAllOrders() throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = """
            SELECT
                o.order_id,
                o.order_date,
                o.total_price,
                o.order_status,
                u.user_id,
                u.name,
                u.email,
                u.phone_number,
                u.account_balance,
                u.is_admin,
                p.is_paid
            FROM public.orders o
            JOIN public.users u ON o.user_id = u.user_id
            LEFT JOIN public.payment p ON o.order_id = p.order_id
            ORDER BY o.order_date DESC
            """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getInt("account_balance"),
                        rs.getBoolean("is_admin")
                );

                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getInt("total_price"),
                        rs.getString("order_status")
                );

                order.setUser(user);

                // Opret Payment og tilknyt den til Order
                Payment payment = new Payment(
                        order,
                        rs.getInt("total_price"),
                        user,
                        rs.getBoolean("is_paid")
                );

                order.setPayment(payment);
                orders.add(order);
            }

            return orders;

        } catch (SQLException e) {
            logger.error("Kunne ikke hente ordrer.", e);
            throw new DatabaseException(e.getMessage());
        }
    }

    public Order getOrderById(int orderId) throws DatabaseException {
        String sql = """
        SELECT o.*, u.user_id, u.name, u.email, u.phone_number,
               u.account_balance, u.is_admin,
               COALESCE(p.is_paid, false) AS is_paid
        FROM public.orders o
        JOIN public.users u ON o.user_id = u.user_id
        LEFT JOIN public.payment p ON o.order_id = p.order_id
        WHERE o.order_id = ?
        """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getInt("account_balance"),
                            rs.getBoolean("is_admin")
                    );

                    Order order = new Order(
                            rs.getInt("order_id"),
                            rs.getTimestamp("order_date").toLocalDateTime(),
                            rs.getInt("total_price"),
                            rs.getString("order_status")
                    );

                    order.setUser(user);

                    Payment payment = new Payment(
                            order,
                            rs.getInt("total_price"),
                            user,
                            rs.getBoolean("is_paid")
                    );

                    order.setPayment(payment);

                    String orderlineSql = """
                    SELECT ol.orderline_id, ol.quantity,
                    b.base_id, b.base_flavor, b.base_price,
                    t.topping_id, t.topping_flavor, t.topping_price
                    FROM public.orderline ol
                    JOIN public.cupcake_base b ON ol.base_id = b.base_id
                    JOIN public.cupcake_topping t ON ol.topping_id = t.topping_id
                    WHERE ol.order_id = ?
                    """;

                    try (PreparedStatement orderlinePs = connection.prepareStatement(orderlineSql)) {
                        orderlinePs.setInt(1, orderId);

                        try (ResultSet orderlineRs = orderlinePs.executeQuery()) {
                            while (orderlineRs.next()) {

                                CupcakeBase base = new CupcakeBase(
                                        orderlineRs.getInt("base_id"),
                                        orderlineRs.getString("base_flavor"),
                                        orderlineRs.getInt("base_price")
                                );

                                CupcakeTopping topping = new CupcakeTopping(
                                        orderlineRs.getInt("topping_id"),
                                        orderlineRs.getString("topping_flavor"),
                                        orderlineRs.getInt("topping_price")
                                );

                                Orderline orderline = new Orderline(
                                        base,
                                        topping,
                                        orderlineRs.getInt("quantity")
                                );

                                orderline.setOrderlineId(
                                        orderlineRs.getInt("orderline_id")
                                );

                                order.getOrderlines().add(orderline);
                            }
                        }
                    }

                    return order;
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("Kunne ikke hente ordren.", e);
            throw new DatabaseException(e.getMessage());
        }
    }


    public void updateOrderStatus(int orderId, String status) throws DatabaseException {
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);

            if (ps.executeUpdate() == 0) {
                throw new DatabaseException("Ordren blev ikke fundet.");
            }

        } catch (SQLException e) {
            logger.error("Kunne ikke ændre ordrestatus.", e);
            throw new DatabaseException(e.getMessage());
        }
    }

}
