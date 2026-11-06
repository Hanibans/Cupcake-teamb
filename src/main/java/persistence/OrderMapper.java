package persistence;

import entities.Order;
import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.sql.*;

public class OrderMapper {
    ConnectionPool connectionPool;
    private static final Logger logger = LoggerFactory.getLogger(OrderMapper.class);

    public OrderMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int addOrder(User user, Order order) throws DatabaseException {

        String sql = """
            INSERT INTO public."Order"
            (user_id, order_date, total_price, order_status)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, user.getUserId());
            ps.setTimestamp(2, Timestamp.valueOf(order.getDate()));
            ps.setInt(3, order.getPrice());
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
            INSERT INTO public."Orderline"
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
}
