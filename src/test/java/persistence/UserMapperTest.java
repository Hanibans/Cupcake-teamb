package persistence;

import entities.User;
import exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private static ConnectionPool connectionPool;
    private static UserMapper userMapper;

    @BeforeAll
    static void setUpClass() {
        connectionPool = TestDatabase.getPool();
        userMapper = new UserMapper(connectionPool);
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            // payment og orders peger på brugeren så de skal væk først
            stmt.execute("DELETE FROM test.payment");
            stmt.execute("DELETE FROM test.orderline");
            stmt.execute("DELETE FROM test.orders");
            stmt.execute("DELETE FROM test.users");

            stmt.execute("ALTER SEQUENCE test.users_user_id_seq RESTART WITH 1");

            stmt.execute("""
                INSERT INTO test.users (is_admin, name, email, password, phone_number, account_balance) VALUES
                (true,  'Admin', 'admin@cupcake.dk', 'admin1234', '11111111', 0),
                (false, 'Julie', 'julie@test.dk',    'julie1234', '22222222', 250),
                (false, 'Mads',  'mads@test.dk',     'mads12345', '33333333', 50)
                """);

        } catch (SQLException e) {
            fail("Database setup failed: " + e.getMessage());
        }
    }

    @Test
    void createUser() throws DatabaseException {
        User user = new User("Test", "new@test.dk", "44444444", "password1");

        User created = userMapper.createUser(user);

        assertEquals(4, created.getUserId());
        assertEquals(100, created.getAccountBalance());
    }

    @Test
    void createUserWithSameEmail() {
        User user = new User("Kopi", "julie@test.dk", "55555555", "password1");

        // email er unik i databasen
        assertThrows(DatabaseException.class, () -> userMapper.createUser(user));
    }

    @Test
    void getUserByMail() throws DatabaseException {
        User user = userMapper.getUserByMail("julie@test.dk");

        assertEquals(2, user.getUserId());
        assertEquals("Julie", user.getName());
        assertEquals("22222222", user.getPhoneNumber());
        assertEquals(250, user.getAccountBalance());
        assertFalse(user.isAdmin());
    }

    @Test
    void getUserByMailAdmin() throws DatabaseException {
        User user = userMapper.getUserByMail("admin@cupcake.dk");

        assertTrue(user.isAdmin());
    }

    @Test
    void getUserByMailNotFound() throws DatabaseException {
        assertNull(userMapper.getUserByMail("findesikke@test.dk"));
    }

    @Test
    void getAllUsers() throws DatabaseException {
        assertEquals(3, userMapper.getAllUsers().size());
    }

    @Test
    void updateAccountBalance() throws DatabaseException {
        userMapper.updateAccountBalance(2, 200);

        User user = userMapper.getUserByMail("julie@test.dk");
        assertEquals(200, user.getAccountBalance());
    }

    @Test
    void updateAccountBalanceUserNotFound() {
        assertThrows(DatabaseException.class, () -> userMapper.updateAccountBalance(99, 200));
    }
}