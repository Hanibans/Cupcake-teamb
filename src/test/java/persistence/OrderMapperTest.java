package persistence;

import entities.CupcakeBase;
import entities.CupcakeTopping;
import entities.Order;
import entities.Orderline;
import entities.User;
import exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private static ConnectionPool connectionPool;
    private static OrderMapper orderMapper;

    @BeforeAll
    static void setUpClass() {
        connectionPool = TestDatabase.getPool();
        orderMapper = new OrderMapper(connectionPool);
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("DELETE FROM test.payment");
            stmt.execute("DELETE FROM test.orderline");
            stmt.execute("DELETE FROM test.orders");
            stmt.execute("DELETE FROM test.users");
            stmt.execute("DELETE FROM test.cupcake_base");
            stmt.execute("DELETE FROM test.cupcake_topping");

            stmt.execute("ALTER SEQUENCE test.orders_order_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE test.orderline_orderline_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE test.users_user_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE test.cupcake_base_base_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE test.cupcake_topping_topping_id_seq RESTART WITH 1");

            // En order kræver en bruger og en orderline kræver en base og topping
            stmt.execute("""
                INSERT INTO test.users (name, email, password, phone_number, account_balance) VALUES
                ('Julie', 'julie@test.dk', 'julie1234', '22222222', 250)
                """);
            stmt.execute("INSERT INTO test.cupcake_base (base_flavor, base_price) VALUES ('Chocolate', 5)");
            stmt.execute("INSERT INTO test.cupcake_topping (topping_flavor, topping_price) VALUES ('Lemon', 8)");

        } catch (SQLException e) {
            fail("Database setup failed: " + e.getMessage());
        }
    }

    @Test
    void addOrder() throws DatabaseException {
        User user = new User(1, "julie@test.dk", "julie1234");
        Order order = new Order(LocalDateTime.now(), 26, "Pending");

        int orderId = orderMapper.addOrder(user, order);

        assertEquals(1, orderId);
    }

    @Test
    void addOrderUserNotFound() {
        User user = new User(99, "findesikke@test.dk", "1234");
        Order order = new Order(LocalDateTime.now(), 26, "Pending");

        // foreign key på orders.user_id afviser det fordi brugeren ikke eksisterer
        assertThrows(DatabaseException.class, () -> orderMapper.addOrder(user, order));
    }

    @Test
    void addOrderline() throws DatabaseException, SQLException {
        User user = new User(1, "julie@test.dk", "julie1234");
        Order order = new Order(LocalDateTime.now(), 26, "Pending");
        order.setOrderId(orderMapper.addOrder(user, order));

        Orderline orderline = new Orderline(
                new CupcakeBase(1, "Chocolate", 5),
                new CupcakeTopping(1, "Lemon", 8),
                2);

        orderMapper.addOrderline(order, orderline);

        // addOrderline returnerer ingenting så vi tjekker databasen direkte
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT base_id, topping_id, quantity FROM test.orderline WHERE order_id = 1")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt("base_id"));
            assertEquals(1, rs.getInt("topping_id"));
            assertEquals(2, rs.getInt("quantity"));
        }
    }

    @Test
    void addOrderlineOrderNotFound() {
        Order order = new Order(LocalDateTime.now(), 0, "Pending");
        order.setOrderId(99);

        Orderline orderline = new Orderline(
                new CupcakeBase(1, "Chocolate", 5),
                new CupcakeTopping(1, "Lemon", 8),
                1);

        // order 99 eksisterer ikke så foreign key på orderline.order_id afviser det
        assertThrows(DatabaseException.class, () -> orderMapper.addOrderline(order, orderline));
    }
}